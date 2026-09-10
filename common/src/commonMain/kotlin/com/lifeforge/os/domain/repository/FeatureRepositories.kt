package com.lifeforge.os.domain.repository

import com.lifeforge.os.domain.model.Food
import com.lifeforge.os.domain.model.Habit
import com.lifeforge.os.domain.model.JournalEntry
import com.lifeforge.os.domain.model.Meal
import com.lifeforge.os.domain.model.MealItem
import com.lifeforge.os.domain.model.Note
import com.lifeforge.os.domain.model.Routine
import com.lifeforge.os.domain.model.Task
import com.lifeforge.os.domain.model.WaterLog
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun observeAll(): Flow<List<Food>>
    fun observeFavorites(): Flow<List<Food>>
    suspend fun search(query: String): List<Food>
    suspend fun save(food: Food)
    suspend fun delete(id: String)
    suspend fun touchRecentlyUsed(id: String)
}

interface NutritionRepository {
    fun observeMeals(date: Long): Flow<List<Meal>>
    fun observeDay(date: Long): Flow<List<Meal>>
    fun observeHistory(start: Long, end: Long): Flow<List<Meal>>
    suspend fun getMeal(id: String): Meal?
    suspend fun saveMeal(meal: Meal)
    suspend fun addItemToMeal(mealId: String, item: MealItem): Meal?
    suspend fun deleteMeal(id: String)
}

interface WaterRepository {
    fun observeDay(date: Long): Flow<List<WaterLog>>
    fun observeRange(start: Long, end: Long): Flow<List<WaterLog>>
    suspend fun addWater(amountMl: Int, date: Long, time: Long)
    suspend fun removeWater(id: String)
    suspend fun getTotalForDay(date: Long): Int
}

interface HabitRepository {
    fun observeAll(): Flow<List<Habit>>
    fun observeActive(): Flow<List<Habit>>
    fun observeDayLog(date: Long): Flow<Map<String, Boolean>>
    suspend fun getById(id: String): Habit?
    suspend fun save(habit: Habit)
    suspend fun toggleCompletion(habitId: String, date: Long): Boolean
    suspend fun delete(id: String)
    suspend fun getStreak(habitId: String): Pair<Int, Int> // current, best
}

interface RoutineRepository {
    fun observeAll(): Flow<List<Routine>>
    fun observeToday(): Flow<List<Routine>>
    suspend fun getById(id: String): Routine?
    suspend fun save(routine: Routine)
    suspend fun completeItem(itemId: String, completed: Boolean)
    suspend fun delete(id: String)
}

interface TaskRepository {
    fun observeTasks(date: Long? = null): Flow<List<Task>>
    fun observeOpen(): Flow<List<Task>>
    fun observeCompleted(): Flow<List<Task>>
    suspend fun getById(id: String): Task?
    suspend fun save(task: Task)
    suspend fun toggleCompleted(id: String): Task?
    suspend fun delete(id: String)
}

interface GoalRepository {
    fun observeAll(): Flow<List<com.lifeforge.os.domain.model.Goal>>
    fun observeActive(): Flow<List<com.lifeforge.os.domain.model.Goal>>
    suspend fun getById(id: String): com.lifeforge.os.domain.model.Goal?
    suspend fun save(goal: com.lifeforge.os.domain.model.Goal)
    suspend fun delete(id: String)
}

interface JournalRepository {
    fun observeAll(): Flow<List<JournalEntry>>
    fun observeMonth(year: Int, month: Int): Flow<List<JournalEntry>>
    fun observePrivate(): Flow<List<JournalEntry>>
    suspend fun getById(id: String): JournalEntry?
    suspend fun save(entry: JournalEntry)
    suspend fun delete(id: String)
    suspend fun search(query: String): List<JournalEntry>
}

interface NoteRepository {
    fun observeAll(): Flow<List<Note>>
    fun observePinned(): Flow<List<Note>>
    fun observePrivate(): Flow<List<Note>>
    fun observeByType(type: com.lifeforge.os.domain.model.NoteType): Flow<List<Note>>
    suspend fun getById(id: String): Note?
    suspend fun save(note: Note)
    suspend fun delete(id: String)
    suspend fun togglePinned(id: String)
    suspend fun search(query: String): List<Note>
}

interface BookRepository {
    fun observeAll(): Flow<List<com.lifeforge.os.domain.model.Book>>
    fun observeByStatus(status: com.lifeforge.os.domain.model.BookStatus): Flow<List<com.lifeforge.os.domain.model.Book>>
    fun observeReading(): Flow<List<com.lifeforge.os.domain.model.Book>>
    suspend fun getById(id: String): com.lifeforge.os.domain.model.Book?
    suspend fun save(book: com.lifeforge.os.domain.model.Book)
    suspend fun updateReadingProgress(id: String, page: Int): com.lifeforge.os.domain.model.Book?
    suspend fun delete(id: String)
}

interface CourseRepository {
    fun observeAll(): Flow<List<com.lifeforge.os.domain.model.Course>>
    fun observeInProgress(): Flow<List<com.lifeforge.os.domain.model.Course>>
    suspend fun getById(id: String): com.lifeforge.os.domain.model.Course?
    suspend fun save(course: com.lifeforge.os.domain.model.Course)
    suspend fun completeLesson(courseId: String, lessonId: String): com.lifeforge.os.domain.model.Course?
    suspend fun updateLessonPosition(courseId: String, lessonId: String, positionSeconds: Int)
    suspend fun delete(id: String)
}

interface MediaRepository {
    fun observeAll(): Flow<List<com.lifeforge.os.domain.model.MediaItem>>
    fun observeByType(type: com.lifeforge.os.domain.model.MediaType): Flow<List<com.lifeforge.os.domain.model.MediaItem>>
    fun observeWatching(): Flow<List<com.lifeforge.os.domain.model.MediaItem>>
    fun observeFavorites(): Flow<List<com.lifeforge.os.domain.model.MediaItem>>
    suspend fun getById(id: String): com.lifeforge.os.domain.model.MediaItem?
    suspend fun save(item: com.lifeforge.os.domain.model.MediaItem)
    suspend fun delete(id: String)
}

interface RecoveryRepository {
    fun observeEntries(): Flow<List<com.lifeforge.os.domain.model.RecoveryEntry>>
    suspend fun saveEntry(entry: com.lifeforge.os.domain.model.RecoveryEntry)
    suspend fun getCurrentStreak(): Int
    suspend fun getBestStreak(): Int
    suspend fun getSuccessfulDays(): Int
    suspend fun getRelapses(): Int
    fun observeTasks(): Flow<List<com.lifeforge.os.domain.model.RecoveryTask>>
    suspend fun toggleTask(taskId: String, date: Long, completed: Boolean)
}

interface TimerRepository {
    fun observePresets(): Flow<List<com.lifeforge.os.domain.model.TimerPreset>>
    suspend fun getById(id: String): com.lifeforge.os.domain.model.TimerPreset?
    suspend fun save(preset: com.lifeforge.os.domain.model.TimerPreset)
    suspend fun delete(id: String)
}