package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.domain.model.Exercise
import com.lifeforge.os.domain.model.WorkoutProgram
import com.lifeforge.os.domain.model.WorkoutSession
import com.lifeforge.os.domain.model.WorkoutSetResult
import com.lifeforge.os.domain.repository.BodyMeasurementEntry
import com.lifeforge.os.domain.repository.BodyTrackingRepository
import com.lifeforge.os.domain.repository.ExerciseRepository
import com.lifeforge.os.domain.repository.WorkoutProgramRepository
import com.lifeforge.os.domain.repository.WorkoutRepository
import com.lifeforge.os.sync.SyncStatus
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class ExerciseRepositoryImpl(
    private val db: LifeForgeDb,
) : ExerciseRepository {

    override fun observeAll(): Flow<List<Exercise>> =
        db.q.selectAllExercises().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Exercise>> =
        db.q.selectFavoriteExercises().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override fun count(): Flow<Int> =
        db.q.countExercises().asFlow().mapToOneOrNull(Dispatchers.IO).map { it?.toInt() ?: 0 }

    override suspend fun getById(id: String): Exercise? =
        db.q.selectExerciseById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun search(query: String): List<Exercise> =
        db.q.selectExercisesByName(query).executeAsList().map { it.toDomain() }

    override suspend fun save(exercise: Exercise) {
        val params = exercise.toDbParams()
        db.q.insertExercise(
            id = params.id,
            name = params.name,
            nameAr = params.nameAr,
            nameEn = params.nameEn,
            mainMuscle = params.mainMuscle,
            secondaryMuscles = params.secondaryMuscles,
            equipment = params.equipment,
            difficulty = params.difficulty,
            description = params.description,
            instructions = params.instructions,
            tips = params.tips,
            imageUrl = params.imageUrl,
            videoUrl = params.videoUrl,
            tags = params.tags,
            isFavorite = params.isFavorite,
            notes = params.notes,
            isCustom = params.isCustom,
            createdAt = params.createdAt,
            updatedAt = params.updatedAt,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteExercise(now, now, id)
    }

    override suspend fun toggleFavorite(id: String) {
        db.q.toggleExerciseFavorite(System.currentTimeMillis(), id)
    }
}

class WorkoutProgramRepositoryImpl(
    private val db: LifeForgeDb,
) : WorkoutProgramRepository {

    override fun observePrograms(): Flow<List<WorkoutProgram>> =
        db.q.selectAllWorkoutPrograms().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): WorkoutProgram? {
        val program = db.q.selectWorkoutProgramById(id).executeAsOneOrNull()?.toDomain() ?: return null
        val days = db.q.selectDaysForProgram(id).executeAsList().map { day ->
            val exercises = db.q.selectExercisesForDay(day.id).executeAsList()
                .map { it.toDomain() }
            day.toDomain(exercises)
        }
        return program.copy(days = days)
    }

    override suspend fun save(program: WorkoutProgram) {
        val now = System.currentTimeMillis()
        db.q.insertWorkoutProgram(
            id = program.id,
            name = program.name,
            nameAr = program.nameAr,
            description = program.description,
            type = program.type.name,
            daysPerWeek = program.days.size.toLong(),
            isActive = program.isActive.asInt(),
            createdAt = program.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        program.days.forEach { day ->
            db.q.insertWorkoutDay(
                id = day.id,
                programId = program.id,
                dayIndex = day.dayIndex.toLong(),
                name = day.name,
                nameAr = day.nameAr,
                notes = day.notes,
                createdAt = now,
                updatedAt = now,
                deviceId = "",
                syncStatus = SyncStatus.Pending.name,
            )
            day.exercises.forEach { we ->
                db.q.insertWorkoutExercise(
                    id = we.id,
                    dayId = day.id,
                    exerciseId = we.exerciseId,
                    exerciseOrder = we.order.toLong(),
                    setsTarget = we.setsTarget.toLong(),
                    repsTarget = we.repsTarget,
                    weightTarget = we.weightTarget,
                    restSeconds = we.restSeconds.toLong(),
                    notes = we.notes,
                    createdAt = now,
                    updatedAt = now,
                    deviceId = "",
                    syncStatus = SyncStatus.Pending.name,
                )
            }
        }
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteWorkoutProgram(now, now, id)
    }

    override suspend fun setActive(id: String) {
        db.q.setActiveWorkoutProgram(id, System.currentTimeMillis())
    }
}

class WorkoutRepositoryImpl(
    private val db: LifeForgeDb,
) : WorkoutRepository {

    override fun observeActiveSession(): Flow<WorkoutSession?> =
        db.q.selectActiveWorkoutSession().asFlow().mapToOneOrNull(Dispatchers.IO).mapNotNull { row ->
            row?.let { it.toDomain(sets = emptyList()) }
        }

    override fun observeHistory(): Flow<List<WorkoutSession>> =
        db.q.selectCompletedWorkoutSessions().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<WorkoutSession>> =
        db.q.selectRecentWorkoutSessions(limit.toLong()).asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getSession(id: String): WorkoutSession? {
        val row = db.q.selectWorkoutSessionById(id).executeAsOneOrNull() ?: return null
        val session = row.toDomain(sets = emptyList())
        if (!session.isCompleted) return session

        val setRows = db.q.selectSetsForSession(id).executeAsList()
        val grouped = setRows.groupBy { it.exerciseId }
        val exercises = grouped.map { (exerciseId, sets) ->
            val name = db.q.selectExerciseById(exerciseId).executeAsOneOrNull()?.name ?: exerciseId
            com.lifeforge.os.domain.model.WorkoutExerciseResult(
                exerciseId = exerciseId,
                exerciseName = name,
                sets = sets.map { it.toDomain() },
                totalVolume = sets.sumOf { (it.weight ?: 0.0) * (it.reps?.toDouble() ?: 0.0) },
            )
        }
        return session.copy(exercises = exercises)
    }

    override suspend fun startSession(programId: String?, dayId: String?): String {
        val id = generateId()
        val now = System.currentTimeMillis()
        db.q.insertWorkoutSession(
            id = id,
            programId = programId,
            dayId = dayId,
            name = null,
            startedAt = now,
            durationSeconds = 0,
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        return id
    }

    override suspend fun saveSet(set: WorkoutSetResult) {
        val now = System.currentTimeMillis()
        db.q.insertWorkoutSet(
            id = set.id.ifBlank { generateId() },
            sessionId = set.sessionId,
            exerciseId = set.exerciseId,
            setIndex = set.setIndex.toLong(),
            weight = set.weight,
            reps = set.reps.toLong(),
            rpe = set.rpe,
            restSeconds = set.restSeconds?.toLong(),
            isCompleted = set.isCompleted.asInt(),
            isWarmup = set.isWarmup.asInt(),
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun completeSession(sessionId: String, durationSeconds: Long): WorkoutSession? {
        val now = System.currentTimeMillis()
        val sets = db.q.selectSetsForSession(sessionId).executeAsList()
        val totalVolume = sets.sumOf { (it.weight ?: 0.0) * (it.reps?.toDouble() ?: 0.0) }
        db.q.completeWorkoutSession(
            completedAt = now,
            durationSeconds = durationSeconds,
            totalVolume = totalVolume,
            totalSets = sets.size.toLong(),
            totalReps = sets.sumOf { it.reps?.toLong() ?: 0L },
            updatedAt = now,
            id = sessionId,
        )
        return getSession(sessionId)
    }

    override suspend fun discardSession(sessionId: String) {
        val now = System.currentTimeMillis()
        db.q.discardWorkoutSession(now, now, sessionId)
    }

    override suspend fun deleteSession(sessionId: String) {
        val now = System.currentTimeMillis()
        db.q.deleteWorkoutSession(now, now, sessionId)
    }
}

class BodyTrackingRepositoryImpl(
    private val db: LifeForgeDb,
) : BodyTrackingRepository {

    override fun observeMeasurements(): Flow<List<BodyMeasurementEntry>> =
        db.q.selectMeasurements().asFlow().mapToList(Dispatchers.IO)
            .map { rows ->
                rows.map {
                    BodyMeasurementEntry(
                        id = it.id,
                        date = it.date,
                        weight = it.weight,
                        bodyFat = it.bodyFat,
                        chest = it.chest,
                        waist = it.waist,
                        arms = it.arms,
                        thighs = it.thighs,
                        neck = it.neck,
                    )
                }
            }

    override suspend fun saveMeasurement(entry: BodyMeasurementEntry) {
        val now = System.currentTimeMillis()
        db.q.insertMeasurement(
            id = entry.id.ifBlank { generateId() },
            date = entry.date,
            weight = entry.weight,
            bodyFat = entry.bodyFat,
            chest = entry.chest,
            waist = entry.waist,
            arms = entry.arms,
            thighs = entry.thighs,
            neck = entry.neck,
            shoulders = null,
            calves = null,
            forearms = null,
            notes = null,
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun getLatest(): BodyMeasurementEntry? {
        val row = db.q.selectLatestMeasurement().executeAsOneOrNull() ?: return null
        return BodyMeasurementEntry(
            id = row.id,
            date = row.date,
            weight = row.weight,
            bodyFat = row.bodyFat,
            chest = row.chest,
            waist = row.waist,
            arms = row.arms,
            thighs = row.thighs,
            neck = row.neck,
        )
    }
}