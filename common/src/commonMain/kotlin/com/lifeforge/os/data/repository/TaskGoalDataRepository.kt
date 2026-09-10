package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.domain.model.Goal
import com.lifeforge.os.domain.model.Task
import com.lifeforge.os.domain.repository.GoalRepository
import com.lifeforge.os.domain.repository.TaskRepository
import com.lifeforge.os.sync.SyncStatus
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val db: LifeForgeDb,
) : TaskRepository {

    override fun observeTasks(date: Long?): Flow<List<Task>> {
        val flow: Flow<List<com.lifeforge.os.data.database.Task>> = if (date == null) {
            db.q.selectAllTasks().asFlow().mapToList(Dispatchers.IO)
        } else {
            db.q.selectTasksByDate(date).asFlow().mapToList(Dispatchers.IO)
        }
        return flow.map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeOpen(): Flow<List<Task>> =
        db.q.selectOpenTasks().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toDomain() } }

    override fun observeCompleted(): Flow<List<Task>> =
        db.q.selectCompletedTasks().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): Task? =
        db.q.selectTaskById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun save(task: Task) {
        val now = System.currentTimeMillis()
        db.q.insertTask(
            id = task.id,
            title = task.title,
            description = task.description,
            date = task.date,
            time = task.time,
            priority = task.priority.name,
            category = task.category,
            isRecurring = task.isRecurring.asInt(),
            isCompleted = task.isCompleted.asInt(),
            completedAt = task.completedAt,
            createdAt = task.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun toggleCompleted(id: String): Task? {
        val task = getById(id) ?: return null
        val completed = !task.isCompleted
        val now = System.currentTimeMillis()
        db.q.completeTask(if (completed) 1L else 0L, if (completed) now else null, now, id)
        return getById(id)
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteTask(now, now, id)
    }
}

class GoalRepositoryImpl(
    private val db: LifeForgeDb,
) : GoalRepository {

    override fun observeAll(): Flow<List<Goal>> =
        db.q.selectAllGoals().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeActive(): Flow<List<Goal>> =
        db.q.selectActiveGoals().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): Goal? =
        db.q.selectGoalById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun save(goal: Goal) {
        val now = System.currentTimeMillis()
        db.q.insertGoal(
            id = goal.id,
            title = goal.title,
            description = goal.description,
            category = goal.category.name,
            targetValue = goal.targetValue,
            currentValue = goal.currentValue,
            unit = goal.unit,
            deadline = goal.deadline,
            startDate = now,
            isActive = goal.isActive.asInt(),
            progress = goal.progress,
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteGoal(now, now, id)
    }
}

internal fun com.lifeforge.os.data.database.Goal.toDomain(): Goal = Goal(
    id = id,
    title = title,
    description = description,
    category = com.lifeforge.os.domain.model.GoalCategory.entries.firstOrNull { it.name == category }
        ?: com.lifeforge.os.domain.model.GoalCategory.Personal,
    targetValue = targetValue,
    currentValue = currentValue ?: 0.0,
    unit = unit,
    deadline = deadline,
    isActive = isActive == 1L,
    progress = progress ?: 0.0,
)