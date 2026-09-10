package com.lifeforge.os.domain.repository

import com.lifeforge.os.sync.SyncConflict
import com.lifeforge.os.sync.SyncRecord
import com.lifeforge.os.sync.SyncStatus

/**
 * Tracks per-entity sync state, dirty records, and pending operations.
 */
interface SyncRepository {
    fun observeDirtyCount(): kotlinx.coroutines.flow.Flow<Int>

    suspend fun markDirty(
        entityType: String,
        entityId: String,
        data: String,
        deletedAt: Long? = null,
    )

    suspend fun upsertPendingChange(change: SyncRecord.PendingChange)

    suspend fun getPendingChanges(): List<SyncOperationModel>

    suspend fun updateSyncStatus(
        entityType: String,
        entityId: String,
        status: SyncStatus,
        version: Long,
    )

    suspend fun getSyncStatus(entityType: String, entityId: String): SyncStatus?

    suspend fun recordConflict(conflict: SyncConflict)

    suspend fun resolveConflict(
        entityType: String,
        entityId: String,
        winningVersion: Long,
    )
}

/**
 * A pending sync operation materialized from local state.
 */
data class SyncOperationModel(
    val type: String, // Upsert | Delete
    val entityType: String,
    val entityId: String,
    val data: String,
    val version: Long,
    val deviceId: String,
    val updatedAt: Long,
)