package com.lifeforge.os.sync.engine

import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.randomUuid
import com.lifeforge.os.sync.SyncStatus
import com.lifeforge.os.sync.cloud.CloudSyncProvider
import com.lifeforge.os.sync.cloud.SyncResult
import com.lifeforge.os.sync.conflict.ConflictResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class SyncEngineImpl(
    private val scopeProvider: CoroutineScopeProvider,
    private val syncQueue: SyncQueue,
    private val cloudSyncProvider: CloudSyncProvider,
    private val conflictResolver: ConflictResolver,
    private val onStatusUpdate: suspend (entityType: String, entityId: String, status: SyncStatus, version: Long) -> Unit,
    private val getPendingChanges: suspend () -> List<SyncOperation>,
    private val logger: LifeForgeLogger,
) : SyncEngine {

    private val _pendingOperations = MutableStateFlow(0)
    override val pendingOperations = _pendingOperations.asStateFlow()

    override fun enqueue(operation: SyncOperation) {
        scopeProvider.ioScope.launch {
            syncQueue.add(operation)
            _pendingOperations.value = syncQueue.size()
            processQueue()
        }
    }

    override fun forceSync(entityType: String, entityId: String) {
        scopeProvider.ioScope.launch {
            val op = getPendingChanges().firstOrNull {
                it.entityType == entityType && it.entityId == entityId
            }
            if (op != null) {
                syncQueue.addToFront(op)
                _pendingOperations.value = syncQueue.size()
                processQueue()
            }
        }
    }

    override fun fullSync() {
        scopeProvider.ioScope.launch {
            val ops = getPendingChanges()
            ops.forEach { syncQueue.add(it) }
            _pendingOperations.value = syncQueue.size()
            processQueue()
        }
    }

    private fun processQueue() {
        scopeProvider.ioScope.launch {
            while (syncQueue.isNotEmpty()) {
                val operation = syncQueue.poll() ?: break

                try {
                    val result: SyncResult = when (operation.type) {
                        SyncOperationType.Upsert -> cloudSyncProvider.upsert(operation)
                        SyncOperationType.Delete -> cloudSyncProvider.delete(operation)
                    }

                    if (result.isSuccess) {
                        onStatusUpdate(
                            operation.entityType,
                            operation.entityId,
                            SyncStatus.Synced,
                            result.remoteVersion ?: operation.version,
                        )
                    } else if (result.conflict != null) {
                        conflictResolver.detectConflict(operation, result.conflict)
                    } else {
                        scheduleRetry(operation)
                    }
                } catch (e: Exception) {
                    logger.error("SyncEngine", "Sync operation failed", e)
                    scheduleRetry(operation)
                }

                _pendingOperations.value = syncQueue.size()
            }
        }
    }

    private fun scheduleRetry(operation: SyncOperation) {
        operation.retryCount++
        if (operation.retryCount > maxRetries) {
            logger.warning("SyncEngine", "Dropping operation after retries: ${operation.entityId}")
            return
        }
        val delay = calculateBackoff(operation.retryCount)
        scopeProvider.ioScope.launch {
            kotlinx.coroutines.delay(delay)
            syncQueue.add(operation)
            _pendingOperations.value = syncQueue.size()
        }
    }

    private fun calculateBackoff(retryCount: Int): Long {
        return minOf(1_000L * 2.0.pow(retryCount).toLong(), 300_000L)
    }

    companion object {
        const val maxRetries = 5
    }
}

interface SyncEngine {
    val pendingOperations: kotlinx.coroutines.flow.StateFlow<Int>

    fun enqueue(operation: SyncOperation)
    fun forceSync(entityType: String, entityId: String)
    fun fullSync()
}

enum class SyncOperationType { Upsert, Delete }

enum class SyncPriority { Low, Normal, High }

data class SyncOperation(
    val type: SyncOperationType,
    val entityType: String,
    val entityId: String,
    val data: String,
    val priority: SyncPriority = SyncPriority.Normal,
    val version: Long = System.currentTimeMillis(),
    val deviceId: String = SyncOperation.getDeviceId(),
    var retryCount: Int = 0,
) {
    companion object {
        private var cachedDeviceId: String? = null
        fun getDeviceId(): String =
            cachedDeviceId ?: randomUuid().also { cachedDeviceId = it }
    }
}