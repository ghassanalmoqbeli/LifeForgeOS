package com.lifeforge.os.domain.repository

import com.lifeforge.os.domain.model.Exercise
import com.lifeforge.os.domain.model.WorkoutProgram
import com.lifeforge.os.domain.model.WorkoutSession
import com.lifeforge.os.domain.model.WorkoutSetResult
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeAll(): Flow<List<Exercise>>
    fun observeFavorites(): Flow<List<Exercise>>
    suspend fun getById(id: String): Exercise?
    suspend fun search(query: String): List<Exercise>
    suspend fun save(exercise: Exercise)
    suspend fun delete(id: String)
    suspend fun toggleFavorite(id: String)
    fun count(): Flow<Int>
}

interface WorkoutProgramRepository {
    fun observePrograms(): Flow<List<WorkoutProgram>>
    suspend fun getById(id: String): WorkoutProgram?
    suspend fun save(program: WorkoutProgram)
    suspend fun delete(id: String)
    suspend fun setActive(id: String)
}

interface WorkoutRepository {
    fun observeActiveSession(): Flow<WorkoutSession?>
    fun observeHistory(): Flow<List<WorkoutSession>>
    fun observeRecent(limit: Int): Flow<List<WorkoutSession>>
    suspend fun getSession(id: String): WorkoutSession?
    suspend fun startSession(programId: String?, dayId: String?): String
    suspend fun saveSet(set: WorkoutSetResult)
    suspend fun completeSession(sessionId: String, durationSeconds: Long): WorkoutSession?
    suspend fun discardSession(sessionId: String)
    suspend fun deleteSession(sessionId: String)
}

interface BodyTrackingRepository {
    fun observeMeasurements(): Flow<List<BodyMeasurementEntry>>
    suspend fun saveMeasurement(entry: BodyMeasurementEntry)
    suspend fun getLatest(): BodyMeasurementEntry?
}

/** Lightweight DTO used at the repository boundary. */
data class BodyMeasurementEntry(
    val id: String = "",
    val date: Long,
    val weight: Double? = null,
    val bodyFat: Double? = null,
    val chest: Double? = null,
    val waist: Double? = null,
    val arms: Double? = null,
    val thighs: Double? = null,
    val neck: Double? = null,
) {
    fun hasData(): Boolean = weight != null || bodyFat != null || chest != null || waist != null ||
        arms != null || thighs != null || neck != null
}