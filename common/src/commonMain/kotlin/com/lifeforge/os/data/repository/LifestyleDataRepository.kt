package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.domain.model.Food
import com.lifeforge.os.domain.model.Habit
import com.lifeforge.os.domain.model.Meal
import com.lifeforge.os.domain.model.MealItem
import com.lifeforge.os.domain.model.Routine
import com.lifeforge.os.domain.model.RoutineItem
import com.lifeforge.os.domain.model.WaterLog
import com.lifeforge.os.domain.repository.FoodRepository
import com.lifeforge.os.domain.repository.HabitRepository
import com.lifeforge.os.domain.repository.NutritionRepository
import com.lifeforge.os.domain.repository.RoutineRepository
import com.lifeforge.os.domain.repository.WaterRepository
import com.lifeforge.os.sync.SyncStatus
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

private const val DAY_MS = 24L * 60 * 60 * 1000

class FoodRepositoryImpl(
    private val db: LifeForgeDb,
) : FoodRepository {

    override fun observeAll(): Flow<List<Food>> =
        db.q.selectAllFoods().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Food>> =
        db.q.selectFavoriteFoods().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toDomain() } }

    override suspend fun search(query: String): List<Food> =
        db.q.selectFoodsByName(query, 50).executeAsList().map { it.toDomain() }

    override suspend fun save(food: Food) {
        val now = System.currentTimeMillis()
        db.q.insertFood(
            id = food.id,
            name = food.name,
            nameAr = food.nameAr,
            servingSize = food.servingSize,
            calories = food.facts.calories,
            protein = food.facts.protein,
            carbs = food.facts.carbs,
            fat = food.facts.fat,
            fiber = food.facts.fiber,
            sugar = food.facts.sugar,
            sodium = food.facts.sodium,
            isCustom = food.isCustom.asInt(),
            isFavorite = food.isFavorite.asInt(),
            recentlyUsedAt = null,
            useCount = 0,
            notes = food.notes,
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteFood(now, now, id)
    }

    override suspend fun touchRecentlyUsed(id: String) {
        val now = System.currentTimeMillis()
        db.q.touchFood(now, now, id)
    }
}

class NutritionRepositoryImpl(
    private val db: LifeForgeDb,
) : NutritionRepository {

    private fun loadItems(mealId: String): List<MealItem> =
        db.q.selectMealItems(mealId).executeAsList().map { it.toDomain() }

    override fun observeMeals(date: Long): Flow<List<Meal>> =
        db.q.selectMealsByDate(date).asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row -> row.toDomain(loadItems(row.id)) } }

    override fun observeDay(date: Long): Flow<List<Meal>> = observeMeals(date)

    override fun observeHistory(start: Long, end: Long): Flow<List<Meal>> =
        db.q.selectMealsBetween(start, end).asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row -> row.toDomain(loadItems(row.id)) } }

    override suspend fun getMeal(id: String): Meal? {
        return db.q.selectMealsBetween(0, Long.MAX_VALUE).executeAsList()
            .firstOrNull { it.id == id }?.let { it.toDomain(loadItems(it.id)) }
    }

    override suspend fun saveMeal(meal: Meal) {
        val now = System.currentTimeMillis()
        db.q.insertMeal(
            id = meal.id,
            date = meal.date,
            type = meal.type.name,
            customType = null,
            time = meal.date,
            name = meal.name,
            imageUrl = null,
            notes = meal.notes,
            totalCalories = meal.total.calories,
            totalProtein = meal.total.protein,
            totalCarbs = meal.total.carbs,
            totalFat = meal.total.fat,
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        meal.items.forEach { item ->
            db.q.insertMealItem(
                id = item.id.ifBlank { generateId() },
                mealId = meal.id,
                foodId = item.foodId,
                servingSize = item.servingMultiplier,
                calories = item.facts.calories,
                protein = item.facts.protein,
                carbs = item.facts.carbs,
                fat = item.facts.fat,
                createdAt = now,
                updatedAt = now,
                deviceId = "",
                syncStatus = SyncStatus.Pending.name,
            )
        }
    }

    override suspend fun addItemToMeal(mealId: String, item: MealItem): Meal? {
        val now = System.currentTimeMillis()
        db.q.insertMealItem(
            id = item.id.ifBlank { generateId() },
            mealId = mealId,
            foodId = item.foodId,
            servingSize = item.servingMultiplier,
            calories = item.facts.calories,
            protein = item.facts.protein,
            carbs = item.facts.carbs,
            fat = item.facts.fat,
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        return getMeal(mealId)
    }

    override suspend fun deleteMeal(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteMeal(now, now, id)
    }
}

class WaterRepositoryImpl(
    private val db: LifeForgeDb,
) : WaterRepository {

    override fun observeDay(date: Long): Flow<List<WaterLog>> =
        db.q.selectWaterByDate(date).asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeRange(start: Long, end: Long): Flow<List<WaterLog>> =
        db.q.selectWaterBetween(start, end).asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun addWater(amountMl: Int, date: Long, time: Long) {
        val now = System.currentTimeMillis()
        db.q.insertWaterLog(
            id = generateId(),
            date = date,
            time = time,
            amountMl = amountMl.toLong(),
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun removeWater(id: String) {
        db.q.deleteWaterLog(id)
    }

    override suspend fun getTotalForDay(date: Long): Int =
        db.q.sumWaterByDate(date).executeAsOneOrNull()?.toInt() ?: 0
}

class HabitRepositoryImpl(
    private val db: LifeForgeDb,
) : HabitRepository {

    override fun observeAll(): Flow<List<Habit>> =
        db.q.selectAllHabits().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toDomain() } }

    override fun observeActive(): Flow<List<Habit>> =
        db.q.selectActiveHabits().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toDomain() } }

    override fun observeDayLog(date: Long): Flow<Map<String, Boolean>> =
        db.q.selectActiveHabits().asFlow().mapToList(Dispatchers.IO)
            .map { rows ->
                rows.mapNotNull { row ->
                    val log = db.q.selectHabitLog(row.id, date).executeAsOneOrNull()
                    row.id to (log?.isCompleted == 1L)
                }.toMap()
            }

    override suspend fun getById(id: String): Habit? =
        db.q.selectAllHabits().executeAsList().firstOrNull { it.id == id }?.toDomain()

    override suspend fun save(habit: Habit) {
        val now = System.currentTimeMillis()
        db.q.insertHabit(
            id = habit.id,
            name = habit.name,
            nameAr = habit.nameAr,
            icon = habit.icon,
            color = null,
            description = habit.description,
            frequency = habit.frequency.name,
            targetValue = habit.targetValue,
            targetUnit = habit.targetUnit,
            reminderTime = null,
            isActive = habit.isActive.asInt(),
            startDate = habit.startDate,
            currentStreak = habit.currentStreak.toLong(),
            bestStreak = habit.bestStreak.toLong(),
            totalCompletions = habit.totalCompletions.toLong(),
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun toggleCompletion(habitId: String, date: Long): Boolean {
        val existing = db.q.selectHabitLog(habitId, date).executeAsOneOrNull()
        val now = System.currentTimeMillis()
        val logId = existing?.id ?: generateId()
        val isCompleted = existing == null || existing.isCompleted != 1L
        db.q.insertOrReplaceHabitLog(
            id = logId,
            habitId = habitId,
            date = date,
            value_ = null,
            isCompleted = if (isCompleted) 1L else 0L,
            notes = null,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        val (current, best) = getStreak(habitId)
        val total = db.q.selectHabitLogsAfter(habitId, 0).executeAsList().count { it.isCompleted == 1L }.toLong()
        db.q.updateHabitStreak(current.toLong(), best.toLong(), total, now, habitId)
        return isCompleted
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteHabit(now, now, id)
    }

    override suspend fun getStreak(habitId: String): Pair<Int, Int> {
        val logs = db.q.selectHabitLogsAfter(habitId, 0).executeAsList()
            .filter { it.isCompleted == 1L }
            .sortedByDescending { it.date }
        if (logs.isEmpty()) return 0 to 0

        var current = 0
        var best = 0
        var streak = 0
        var prevDate: Long? = null
        for (log in logs) {
            if (prevDate == null || prevDate - log.date <= DAY_MS + 1000) {
                streak++
            } else {
                streak = 1
            }
            if (streak > best) best = streak
            prevDate = log.date
        }
        val lastDate = logs.first().date
        val todayStart = System.currentTimeMillis() / DAY_MS * DAY_MS
        if (lastDate < todayStart - DAY_MS) current = 0 else current = streak
        return current to best
    }
}

class RoutineRepositoryImpl(
    private val db: LifeForgeDb,
) : RoutineRepository {

    private fun loadItems(routineId: String): List<RoutineItem> =
        db.q.selectRoutineItems(routineId).executeAsList().map { it.toDomain() }

    override fun observeAll(): Flow<List<Routine>> =
        db.q.selectAllRoutines().asFlow().mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row -> row.toDomain(loadItems(row.id)) } }

    override fun observeToday(): Flow<List<Routine>> = observeAll()

    override suspend fun getById(id: String): Routine? =
        db.q.selectAllRoutines().executeAsList().firstOrNull { it.id == id }?.let { it.toDomain(loadItems(it.id)) }

    override suspend fun save(routine: Routine) {
        val now = System.currentTimeMillis()
        db.q.insertRoutine(
            id = routine.id,
            name = routine.name,
            nameAr = routine.nameAr,
            type = routine.type.name,
            description = null,
            isActive = routine.isActive.asInt(),
            createdAt = now,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        routine.items.forEach { item ->
            db.q.insertRoutineItem(
                id = item.id.ifBlank { generateId() },
                routineId = routine.id,
                name = item.name,
                time = item.time,
                durationMinutes = item.durationMinutes?.toLong(),
                itemOrder = item.order.toLong(),
                isCompleted = item.isCompleted.asInt(),
                createdAt = now,
                updatedAt = now,
                deviceId = "",
                syncStatus = SyncStatus.Pending.name,
            )
        }
    }

    override suspend fun completeItem(itemId: String, completed: Boolean) {
        db.q.completeRoutineItem(if (completed) 1L else 0L, System.currentTimeMillis(), itemId)
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteRoutine(now, now, id)
    }
}