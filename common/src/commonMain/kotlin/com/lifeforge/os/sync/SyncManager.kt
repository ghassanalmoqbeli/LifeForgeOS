package com.lifeforge.os.sync

import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.core.preferences.PreferencesManager
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.data.repository.SyncRepository
import com.lifeforge.os.sync.cloud.CloudSyncProvider
import com.lifeforge.os.sync.conflict.ConflictResolver
import com.lifeforge.os.sync.engine.SyncEngine
import com.lifeforge.os.sync.engine.SyncQueue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SyncStatus { Synced, Syncing, Offline, Pending, Conflict, Error }

/**
 * Local bookkeeping record for a single entity's sync state.
 */
sealed interface SyncRecord {
    val entityType: String
    val entityId: String

    data class Status(
        override val entityType: String,
        override val entityId: String,
        val status: SyncStatus,
        val version: Long,
        val deviceId: String,
        val updatedAt: Long,
    ) : SyncRecord

    data class PendingChange(
        override val entityType: String,
        override val entityId: String,
        val version: Long,
        val deviceId: String,
        val updatedAt: Long,
        val deletedAt: Long?,
        val data: String,
    ) : SyncRecord
}

/**
 * A sync conflict between local and remote copies.
 */
data class SyncConflict(
    val entityType: String,
    val entityId: String,
    val localVersion: Long,
    val remoteVersion: Long,
    val localData: String,
    val remoteData: String,
)

enum class ConflictResolution {
    KeepLocal, KeepRemote, Merge, Manual,
}

class SyncManagerImpl(
    private val preferencesManager: PreferencesManager,
    private val scopeProvider: CoroutineScopeProvider,
    private val syncEngine: SyncEngine,
    private val syncQueue: SyncQueue,
    private val conflictResolver: ConflictResolver,
    private val cloudSyncProvider: CloudSyncProvider,
    private val syncRepository: SyncRepository,
    private val logger: LifeForgeLogger,
) : SyncManager {

    private val _syncStatus = MutableStateFlow(SyncStatus.Offline)
    override val syncStatus = _syncStatus.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    override val pendingCount = _pendingCount.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    override val isSyncing = _isSyncing.asStateFlow()

    override fun initialize(context: Any?) {
        scopeProvider.ioScope.launch {
            val syncEnabled = preferencesManager.syncEnabled.first()
            if (syncEnabled) {
                awaitAuthAndSync()
            }
        }
    }

    override suspend fun syncNow() {
        if (!_isSyncing.value) {
            awaitAuthAndSync()
        }
    }

    override suspend fun forceSync(entityType: String, entityId: String) {
        syncEngine.forceSync(entityType, entityId)
    }

    override fun getSyncStatus(entityType: String, entityId: String): SyncStatus? {
        return syncRepository.getSyncStatus(entityType, entityId)
    }

    override fun getPendingConflicts() = conflictResolver.getPendingConflicts()

    override fun resolveConflict(conflict: SyncConflict, resolution: ConflictResolution) {
        scopeProvider.ioScope.launch {
            conflictResolver.resolve(conflict, resolution)
        }
    }

    override fun enableSync() {
        scopeProvider.ioScope.launch {
            preferencesManager.setSyncEnabled(true)
            awaitAuthAndSync()
        }
    }

    override fun disableSync() {
        scopeProvider.ioScope.launch {
            preferencesManager.setSyncEnabled(false)
            _syncStatus.value = SyncStatus.Offline
        }
    }

    override suspend fun isSyncEnabled(): Boolean = preferencesManager.syncEnabled.first()

    private suspend fun awaitAuthAndSync() {
        if (_isSyncing.value) return
        _isSyncing.value = true
        _syncStatus.value = SyncStatus.Syncing

        try {
            val authResult = cloudSyncProvider.ensureAuthenticated()
            if (authResult.isSuccess) {
                syncEngine.fullSync()
                _syncStatus.value = SyncStatus.Synced
                preferencesManager.setLastSyncTime(System.currentTimeMillis())
            } else {
                _syncStatus.value = SyncStatus.Error
                logger.error("SyncManager", "Auth failed: ${authResult.getError()?.message}")
            }
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error
            logger.error("SyncManager", "Sync failed", e)
        } finally {
            _isSyncing.value = false
        }
    }
}

interface SyncManager {
    val syncStatus: kotlinx.coroutines.flow.StateFlow<SyncStatus>
    val pendingCount: kotlinx.coroutines.flow.StateFlow<Int>
    val isSyncing: kotlinx.coroutines.flow.StateFlow<Boolean>

    fun initialize(context: Any?)
    suspend fun syncNow()
    suspend fun forceSync(entityType: String, entityId: String)
    fun getSyncStatus(entityType: String, entityId: String): SyncStatus?
    fun getPendingConflicts(): List<SyncConflict>
    fun resolveConflict(conflict: SyncConflict, resolution: ConflictResolution)
    fun enableSync()
    fun disableSync()
    suspend fun isSyncEnabled(): Boolean
}