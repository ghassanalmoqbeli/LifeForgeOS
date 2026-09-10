package com.lifeforge.os.sync.conflict

import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.sync.ConflictResolution
import com.lifeforge.os.sync.SyncConflict
import com.lifeforge.os.sync.SyncStatus
import com.lifeforge.os.sync.cloud.RemoteRecord
import com.lifeforge.os.sync.engine.SyncOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConflictResolverImpl(
    private val scopeProvider: CoroutineScopeProvider,
    private val onResolve: suspend (SyncConflict, ConflictResolution, Long) -> Unit,
    private val logger: LifeForgeLogger,
) : ConflictResolver {

    private val _pendingConflicts = MutableStateFlow<List<SyncConflict>>(emptyList())
    override val pendingConflicts = _pendingConflicts.asStateFlow()

    override fun detectConflict(operation: SyncOperation, remote: RemoteRecord) {
        scopeProvider.ioScope.launch {
            val conflict = SyncConflict(
                entityType = operation.entityType,
                entityId = operation.entityId,
                localVersion = operation.version,
                remoteVersion = remote.version,
                localData = operation.data,
                remoteData = remote.data,
            )
            addConflict(conflict)
        }
    }

    override fun resolve(conflict: SyncConflict, resolution: ConflictResolution) {
        scopeProvider.ioScope.launch {
            val winningVersion = when (resolution) {
                ConflictResolution.KeepLocal -> conflict.localVersion
                ConflictResolution.KeepRemote -> conflict.remoteVersion
                ConflictResolution.Merge, ConflictResolution.Manual -> {
                    // Merge tries automatic; Manual keeps for user.
                    return@launch
                }
            }
            onResolve(conflict, resolution, winningVersion)
            removeConflict(conflict)
        }
    }

    override fun getPendingConflicts(): List<SyncConflict> = _pendingConflicts.value

    private fun addConflict(conflict: SyncConflict) {
        val current = _pendingConflicts.value
        if (current.none { it.entityType == conflict.entityType && it.entityId == conflict.entityId }) {
            _pendingConflicts.value = current + conflict
        }
    }

    private fun removeConflict(conflict: SyncConflict) {
        _pendingConflicts.value = _pendingConflicts.value.filterNot {
            it.entityType == conflict.entityType && it.entityId == conflict.entityId
        }
    }
}

interface ConflictResolver {
    val pendingConflicts: kotlinx.coroutines.flow.StateFlow<List<SyncConflict>>

    fun detectConflict(operation: SyncOperation, remote: RemoteRecord)
    fun resolve(conflict: SyncConflict, resolution: ConflictResolution)
    fun getPendingConflicts(): List<SyncConflict>
}