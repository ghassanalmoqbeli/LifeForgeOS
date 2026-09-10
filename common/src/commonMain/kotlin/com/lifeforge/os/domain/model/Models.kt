package com.lifeforge.os.domain.model

import kotlinx.serialization.Serializable

// ============================================================
// GYM / FITNESS MODELS
// ============================================================

@Serializable
enum class Difficulty { Beginner, Intermediate, Advanced, Expert }

@Serializable
enum class Equipment {
    Bodyweight, Barbell, Dumbbell, Kettlebell, Cable, Machine, Bands, Other,
}

@Serializable
enum class MuscleGroup {
    Chest, Back, Shoulders, Biceps, Triceps, Forearms, Quads, Hamstrings, Glutes,
    Calves, Abs, Core, Traps, Neck, FullBody,
}

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val nameEn: String? = null,
    val mainMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val equipment: Equipment = Equipment.Bodyweight,
    val difficulty: Difficulty = Difficulty.Beginner,
    val description: String? = null,
    val instructions: List<String> = emptyList(),
    val tips: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val notes: String? = null,
    val isCustom: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
enum class ProgramType { PushPullLegs, UpperLower, FullBody, Custom }

@Serializable
data class WorkoutProgram(
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val description: String? = null,
    val type: ProgramType = ProgramType.Custom,
    val days: List<WorkoutDay> = emptyList(),
    val isActive: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class WorkoutDay(
    val id: String,
    val programId: String,
    val dayIndex: Int,
    val name: String,
    val nameAr: String? = null,
    val exercises: List<WorkoutExercise> = emptyList(),
    val notes: String? = null,
)

@Serializable
data class WorkoutExercise(
    val id: String,
    val dayId: String,
    val exerciseId: String,
    val order: Int,
    val setsTarget: Int = 3,
    val repsTarget: String = "8-12",
    val weightTarget: String? = null,
    val restSeconds: Int = 90,
    val notes: String? = null,
)

@Serializable
data class WorkoutSession(
    val id: String,
    val programId: String? = null,
    val dayId: String? = null,
    val name: String? = null,
    val startedAt: Long,
    val completedAt: Long? = null,
    val durationSeconds: Long = 0,
    val totalVolume: Double = 0.0,
    val exercises: List<WorkoutExerciseResult> = emptyList(),
    val isCompleted: Boolean = false,
    val notes: String? = null,
)

@Serializable
data class WorkoutExerciseResult(
    val exerciseId: String,
    val exerciseName: String,
    val sets: List<WorkoutSetResult> = emptyList(),
    val totalVolume: Double = 0.0,
)

@Serializable
data class WorkoutSetResult(
    val id: String = "",
    val sessionId: String = "",
    val exerciseId: String,
    val setIndex: Int,
    val weight: Double = 0.0,
    val reps: Int = 0,
    val rpe: Double? = null,
    val restSeconds: Int? = null,
    val isCompleted: Boolean = false,
    val isWarmup: Boolean = false,
)

@Serializable
data class WorkoutRecord(
    val id: String,
    val exerciseId: String,
    val type: RecordType,
    val value: Double,
    val previousValue: Double? = null,
    val achievedAt: Long,
)

@Serializable
enum class RecordType { Weight, Reps, Volume, OneRepMax }

// ============================================================
// NUTRITION MODELS
// ============================================================

@Serializable
enum class MealType { Breakfast, Lunch, Dinner, Snack, Custom }

@Serializable
data class NutritionFacts(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
)

@Serializable
data class Food(
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val servingSize: String,
    val facts: NutritionFacts,
    val isCustom: Boolean = true,
    val isFavorite: Boolean = false,
    val notes: String? = null,
)

@Serializable
data class Meal(
    val id: String,
    val date: Long,
    val type: MealType,
    val name: String? = null,
    val items: List<MealItem> = emptyList(),
    val total: NutritionFacts = NutritionFacts(),
    val notes: String? = null,
)

@Serializable
data class MealItem(
    val id: String,
    val mealId: String,
    val foodId: String,
    val foodName: String,
    val servingMultiplier: Double = 1.0,
    val facts: NutritionFacts = NutritionFacts(),
)

@Serializable
data class WaterLog(
    val id: String,
    val date: Long,
    val time: Long,
    val amountMl: Int,
)

// ============================================================
// HABITS & ROUTINES
// ============================================================

@Serializable
data class Habit(
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val icon: String? = null,
    val description: String? = null,
    val frequency: HabitFrequency = HabitFrequency.Daily,
    val targetValue: Double? = null,
    val targetUnit: String? = null,
    val isActive: Boolean = true,
    val startDate: Long,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompletions: Int = 0,
)

@Serializable
enum class HabitFrequency { Daily, Weekdays, Weekends, Custom }

@Serializable
enum class RoutineType { Morning, Workout, Study, Night, Recovery, Custom }

@Serializable
data class Routine(
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val type: RoutineType = RoutineType.Custom,
    val items: List<RoutineItem> = emptyList(),
    val isActive: Boolean = true,
)

@Serializable
data class RoutineItem(
    val id: String,
    val routineId: String,
    val name: String,
    val time: Long,
    val durationMinutes: Int? = null,
    val order: Int,
    val isCompleted: Boolean = false,
)

// ============================================================
// TASKS & GOALS
// ============================================================

@Serializable
enum class TaskPriority { Low, Medium, High, Urgent }

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val date: Long? = null,
    val time: Long? = null,
    val priority: TaskPriority = TaskPriority.Medium,
    val category: String? = null,
    val isRecurring: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
enum class GoalCategory { Fitness, Nutrition, Reading, Learning, Recovery, Personal, Other }

@Serializable
data class Goal(
    val id: String,
    val title: String,
    val description: String? = null,
    val category: GoalCategory = GoalCategory.Personal,
    val targetValue: Double,
    val currentValue: Double = 0.0,
    val unit: String? = null,
    val deadline: Long? = null,
    val isActive: Boolean = true,
    val progress: Double = 0.0,
)

// ============================================================
// JOURNAL & NOTES
// ============================================================

@Serializable
data class JournalEntry(
    val id: String,
    val date: Long,
    val title: String? = null,
    val content: String,
    val mood: String? = null,
    val energy: Int? = null,
    val stress: Int? = null,
    val tags: List<String> = emptyList(),
    val isPrivate: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
enum class NoteType { Quick, Rich, Checklist, Idea, Personal, Study, Workout, Nutrition, Recovery, Book, Course }

@Serializable
data class Note(
    val id: String,
    val type: NoteType = NoteType.Quick,
    val title: String? = null,
    val content: String? = null,
    val plainText: String? = null,
    val isPinned: Boolean = false,
    val isPrivate: Boolean = false,
    val tags: List<String> = emptyList(),
    val linkedEntityType: String? = null,
    val linkedEntityId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

// ============================================================
// BOOKS & READING
// ============================================================

@Serializable
enum class BookStatus { WantToRead, Reading, Completed, Paused, Dropped }

@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val description: String? = null,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val format: String? = null,
    val totalPages: Int? = null,
    val currentPage: Int = 0,
    val status: BookStatus = BookStatus.WantToRead,
    val progressPercent: Double = 0.0,
    val rating: Double? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

// ============================================================
// COURSES & LEARNING
// ============================================================

@Serializable
enum class CourseStatus { NotStarted, InProgress, Completed, Paused, Dropped }

@Serializable
data class Course(
    val id: String,
    val title: String,
    val provider: String? = null,
    val instructor: String? = null,
    val thumbnailUrl: String? = null,
    val description: String? = null,
    val category: String? = null,
    val sections: List<CourseSection> = emptyList(),
    val status: CourseStatus = CourseStatus.NotStarted,
    val progressPercent: Double = 0.0,
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class CourseSection(
    val id: String,
    val courseId: String,
    val title: String,
    val order: Int,
    val lessons: List<CourseLesson> = emptyList(),
)

@Serializable
data class CourseLesson(
    val id: String,
    val sectionId: String,
    val title: String,
    val order: Int,
    val videoUrl: String? = null,
    val durationMinutes: Int? = null,
    val isCompleted: Boolean = false,
    val lastPositionSeconds: Int = 0,
    val progressPercent: Double = 0.0,
)

// ============================================================
// ENTERTAINMENT
// ============================================================

@Serializable
enum class MediaType { Movie, TVShow, Anime, Game, Video, Podcast, Music }

@Serializable
enum class MediaStatus { WantToWatch, Watching, Completed, Paused, Dropped }

@Serializable
data class MediaItem(
    val id: String,
    val type: MediaType,
    val title: String,
    val titleAr: String? = null,
    val imageUrl: String? = null,
    val backdropUrl: String? = null,
    val description: String? = null,
    val externalUrl: String? = null,
    val status: MediaStatus = MediaStatus.WantToWatch,
    val rating: Double? = null,
    val personalRating: Double? = null,
    val review: String? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val progressPercent: Double = 0.0,
    val createdAt: Long,
    val updatedAt: Long,
)

// ============================================================
// RECOVERY
// ============================================================

@Serializable
data class RecoveryEntry(
    val id: String,
    val date: Long,
    val isRelapse: Boolean = false,
    val trigger: String? = null,
    val mood: String? = null,
    val whatHappened: String? = null,
    val whatLearned: String? = null,
    val notes: String? = null,
    val streakAtTime: Int = 0,
)

@Serializable
data class RecoveryTask(
    val id: String,
    val name: String,
    val category: String? = null,
    val isActive: Boolean = true,
)

// ============================================================
// TIMERS
// ============================================================

@Serializable
enum class TimerType { Countdown, Stopwatch, RestTimer, Pomodoro, CustomInterval }

@Serializable
data class TimerPreset(
    val id: String,
    val name: String,
    val type: TimerType = TimerType.Countdown,
    val phases: List<TimerPhase> = emptyList(),
    val totalSeconds: Int = 0,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
)

@Serializable
data class TimerPhase(
    val name: String,
    val durationSeconds: Int,
    val accent: String? = null,
)

// ============================================================
// ACHIEVEMENTS / SUMMARIES
// ============================================================

@Serializable
data class Achievement(
    val id: String,
    val key: String,
    val category: String,
    val title: String,
    val titleAr: String? = null,
    val icon: String? = null,
    val tier: String = "Bronze",
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Double = 0.0,
)

@Serializable
data class DailySummary(
    val date: Long,
    val workoutCompleted: Boolean = false,
    val workoutVolume: Double = 0.0,
    val caloriesConsumed: Double = 0.0,
    val proteinConsumed: Double = 0.0,
    val waterMl: Int = 0,
    val habitsCompleted: Int = 0,
    val habitsTotal: Int = 0,
    val routineCompleted: Int = 0,
    val routineTotal: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksTotal: Int = 0,
    val readingMinutes: Int = 0,
    val learningMinutes: Int = 0,
)