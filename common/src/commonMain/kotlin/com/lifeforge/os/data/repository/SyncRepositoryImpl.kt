package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.domain.repository.SyncOperationModel
import com.lifeforge.os.domain.repository.SyncRepository
import com.lifeforge.os.sync.SyncConflict
import com.lifeforge.os.sync.SyncRecord
import com.lifeforge.os.sync.SyncStatus
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncRepositoryImpl(
    private val db: LifeForgeDb,
) : SyncRepository {

    override fun observeDirtyCount(): Flow<Int> =
        db.database.lifeForgeDatabaseQueries.selectDirtySyncRecords().asFlow().mapToList(Dispatchers.IO).map { it.size }

    override suspend fun markDirty(
        entityType: String,
        entityId: String,
        data: String,
        deletedAt: Long?,
    ) {
        val now = System.currentTimeMillis()
        db.database.lifeForgeDatabaseQueries.upsertSyncRecord(
            entityType = entityType,
            entityId = entityId,
            status = SyncStatus.Pending.name,
            version = now,
            deviceId = "",
            createdAt = now,
            updatedAt = now,
            deletedAt = deletedAt,
            conflictData = data,
        )
    }

    override suspend fun upsertPendingChange(change: SyncRecord.PendingChange) {
        db.database.lifeForgeDatabaseQueries.upsertSyncRecord(
            entityType = change.entityType,
            entityId = change.entityId,
            status = SyncStatus.Pending.name,
            version = change.version,
            deviceId = change.deviceId,
            createdAt = change.updatedAt,
            updatedAt = change.updatedAt,
            deletedAt = change.deletedAt,
            conflictData = change.data,
        )
    }

    override suspend fun getPendingChanges(): List<SyncOperationModel> {
        return db.database.lifeForgeDatabaseQueries.selectDirtySyncRecords().executeAsList().map { row ->
            SyncOperationModel(
                type = if (row.deletedAt != null) "Delete" else "Upsert",
                entityType = row.entityType,
                entityId = row.entityId,
                data = row.conflictData ?: "",
                version = row.version,
                deviceId = row.deviceId,
                updatedAt = row.updatedAt,
            )
        }
    }

    override suspend fun updateSyncStatus(
        entityType: String,
        entityId: String,
        status: SyncStatus,
        version: Long,
    ) {
        db.database.lifeForgeDatabaseQueries.updateSyncRecordStatus(
            status = status.name,
            version = version,
            updatedAt = System.currentTimeMillis(),
            entityType = entityType,
            entityId = entityId,
        )
    }

    override suspend fun getSyncStatus(entityType: String, entityId: String): SyncStatus? {
        val row = db.database.lifeForgeDatabaseQueries.selectSyncRecord(entityType, entityId)
            .executeAsOneOrNull()
        return row?.let { runCatching { SyncStatus.valueOf(it.status) }.getOrNull() }
    }

    override suspend fun recordConflict(conflict: SyncConflict) {
        db.database.lifeForgeDatabaseQueries.updateSyncRecordStatus(
            status = SyncStatus.Conflict.name,
            version = conflict.remoteVersion,
            updatedAt = System.currentTimeMillis(),
            entityType = conflict.entityType,
            entityId = conflict.entityId,
        )
    }

    override suspend fun resolveConflict(
        entityType: String,
        entityId: String,
        winningVersion: Long,
    ) {
        db.database.lifeForgeDatabaseQueries.updateSyncRecordStatus(
            status = SyncStatus.Synced.name,
            version = winningVersion,
            updatedAt = System.currentTimeMillis(),
            entityType = entityType,
            entityId = entityId,
        )
    }
}