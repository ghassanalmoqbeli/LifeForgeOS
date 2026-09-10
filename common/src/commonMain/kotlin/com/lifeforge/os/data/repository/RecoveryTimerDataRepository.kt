package com.lifeforge.os.data.repository

import kotlinx.serialization.encodeToString

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.domain.model.RecoveryEntry
import com.lifeforge.os.domain.model.RecoveryTask
import com.lifeforge.os.domain.model.TimerPreset
import com.lifeforge.os.domain.repository.RecoveryRepository
import com.lifeforge.os.domain.repository.TimerRepository
import com.lifeforge.os.sync.SyncStatus
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToLong

private const val DAY_MS = 24L * 60 * 60 * 1000

class RecoveryRepositoryImpl(
    private val db: LifeForgeDb,
) : RecoveryRepository {

    override fun observeEntries(): Flow<List<RecoveryEntry>> =
        db.q.selectAllRecoveryEntries().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override suspend fun saveEntry(entry: RecoveryEntry) {
        val now = System.currentTimeMillis()
        db.q.insertRecoveryEntry(
            id = entry.id.ifBlank { generateId() },
            date = entry.date,
            isRelapse = entry.isRelapse.asInt(),
            trigger = entry.trigger,
            mood = entry.mood,
            whatHappened = entry.whatHappened,
            whatLearned = entry.whatLearned,
            notes = entry.notes,
            streakAtTime = entry.streakAtTime.toLong(),
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun getCurrentStreak(): Int {
        val entries = db.q.selectAllRecoveryEntries().executeAsList()
            .sortedByDescending { it.date }
        var streak = 0
        for ((index, entry) in entries.withIndex()) {
            if (entry.isRelapse == 1L) break
            if (index > 0) {
                val expected = entries[index - 1].date - DAY_MS
                if (entry.date < expected - DAY_MS / 2) break
            }
            streak++
        }
        return streak
    }

    override suspend fun getBestStreak(): Int {
        val entries = db.q.selectAllRecoveryEntries().executeAsList()
            .sortedBy { it.date }
        var best = 0
        var current = 0
        var prev: Long? = null
        for (entry in entries) {
            if (entry.isRelapse == 1L) {
                current = 0
                prev = null
            } else {
                val expected = prev?.plus(DAY_MS)
                current = if (expected == null || entry.date >= expected - DAY_MS / 2) current + 1 else 1
                if (current > best) best = current
                prev = entry.date
            }
        }
        return best
    }

    override suspend fun getSuccessfulDays(): Int =
        db.q.selectAllRecoveryEntries().executeAsList().count { it.isRelapse == 0L }

    override suspend fun getRelapses(): Int =
        db.q.selectAllRecoveryEntries().executeAsList().count { it.isRelapse == 1L }

    override fun observeTasks(): Flow<List<RecoveryTask>> =
        db.q.selectAllRecoveryTasks().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override suspend fun toggleTask(taskId: String, date: Long, completed: Boolean) {
        val existing = db.q.selectRecoveryTaskLog(taskId, date).executeAsOneOrNull()
        val now = System.currentTimeMillis()
        db.q.upsertRecoveryTaskLog(
            id = existing?.id ?: generateId(),
            taskId = taskId,
            date = date,
            value_ = null,
            isCompleted = if (completed) 1L else 0L,
            notes = null,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }
}

class TimerRepositoryImpl(
    private val db: LifeForgeDb,
) : TimerRepository {

    override fun observePresets(): Flow<List<TimerPreset>> =
        db.q.selectAllTimerPresets().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): TimerPreset? =
        db.q.selectTimerPresetById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun save(preset: TimerPreset) {
        val now = System.currentTimeMillis()
        db.q.insertTimerPreset(
            id = preset.id,
            name = preset.name,
            type = preset.type.name,
            phases = kotlinx.serialization.json.Json.encodeToString(preset.phases),
            totalDuration = preset.totalSeconds.toLong(),
            soundEnabled = preset.soundEnabled.asInt(),
            vibrationEnabled = preset.vibrationEnabled.asInt(),
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteTimerPreset(now, now, id)
    }
}

private fun Double?.roundToLongOrZero(): Long = this?.roundToLong() ?: 0L