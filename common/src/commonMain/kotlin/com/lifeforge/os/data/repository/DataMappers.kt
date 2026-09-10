package com.lifeforge.os.data.repository

import com.lifeforge.os.domain.model.Book
import com.lifeforge.os.domain.model.BookStatus
import com.lifeforge.os.domain.model.Course
import com.lifeforge.os.domain.model.CourseLesson
import com.lifeforge.os.domain.model.CourseSection
import com.lifeforge.os.domain.model.Difficulty
import com.lifeforge.os.domain.model.Equipment
import com.lifeforge.os.domain.model.Exercise
import com.lifeforge.os.domain.model.Food
import com.lifeforge.os.domain.model.Habit
import com.lifeforge.os.domain.model.HabitFrequency
import com.lifeforge.os.domain.model.JournalEntry
import com.lifeforge.os.domain.model.Meal
import com.lifeforge.os.domain.model.MealItem
import com.lifeforge.os.domain.model.MealType
import com.lifeforge.os.domain.model.MediaItem
import com.lifeforge.os.domain.model.MediaStatus
import com.lifeforge.os.domain.model.MediaType
import com.lifeforge.os.domain.model.MuscleGroup
import com.lifeforge.os.domain.model.Note
import com.lifeforge.os.domain.model.NoteType
import com.lifeforge.os.domain.model.NutritionFacts
import com.lifeforge.os.domain.model.ProgramType
import com.lifeforge.os.domain.model.RecoveryEntry
import com.lifeforge.os.domain.model.RecoveryTask
import com.lifeforge.os.domain.model.Routine
import com.lifeforge.os.domain.model.RoutineItem
import com.lifeforge.os.domain.model.RoutineType
import com.lifeforge.os.domain.model.Task
import com.lifeforge.os.domain.model.TaskPriority
import com.lifeforge.os.domain.model.TimerPreset
import com.lifeforge.os.domain.model.TimerType
import com.lifeforge.os.domain.model.WaterLog
import com.lifeforge.os.domain.model.WorkoutDay
import com.lifeforge.os.domain.model.WorkoutExercise
import com.lifeforge.os.domain.model.WorkoutProgram
import com.lifeforge.os.domain.model.WorkoutSession
import com.lifeforge.os.domain.model.WorkoutSetResult

internal inline fun <reified T : Enum<T>> String?.toEnumOr(default: T): T =
    this?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default

internal fun String?.asCsvList(): List<String> =
    this?.split(",")?.map(String::trim)?.filter(String::isNotBlank) ?: emptyList()

internal fun List<String>.toCsvOrNull(): String? = takeIf { it.isNotEmpty() }?.joinToString(",")

internal fun Boolean.asInt(): Long = if (this) 1L else 0L

// ============================================================
// Workouts
// ============================================================

internal fun com.lifeforge.os.data.database.Exercise.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    nameAr = nameAr,
    nameEn = nameEn,
    mainMuscle = mainMuscle?.toEnumOr(MuscleGroup.FullBody) ?: MuscleGroup.FullBody,
    secondaryMuscles = secondaryMuscles.asCsvList().mapNotNull { runCatching { MuscleGroup.valueOf(it) }.getOrNull() },
    equipment = equipment?.toEnumOr(Equipment.Bodyweight) ?: Equipment.Bodyweight,
    difficulty = difficulty?.toEnumOr(Difficulty.Beginner) ?: Difficulty.Beginner,
    description = description,
    instructions = instructions.asCsvList(),
    tips = tips,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    tags = tags.asCsvList(),
    isFavorite = isFavorite == 1L,
    notes = notes,
    isCustom = isCustom == 1L,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun Exercise.toDbParams(): UpsertExerciseParams = UpsertExerciseParams(
    id = id,
    name = name,
    nameAr = nameAr,
    nameEn = nameEn,
    mainMuscle = mainMuscle.name,
    secondaryMuscles = secondaryMuscles.map { it.name }.toCsvOrNull(),
    equipment = equipment.name,
    difficulty = difficulty.name,
    description = description,
    instructions = instructions.toCsvOrNull(),
    tips = tips,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    tags = tags.toCsvOrNull(),
    isFavorite = isFavorite.asInt(),
    notes = notes,
    isCustom = isCustom.asInt(),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal data class UpsertExerciseParams(
    val id: String, val name: String, val nameAr: String?, val nameEn: String?,
    val mainMuscle: String, val secondaryMuscles: String?, val equipment: String,
    val difficulty: String, val description: String?, val instructions: String?,
    val tips: String?, val imageUrl: String?, val videoUrl: String?, val tags: String?,
    val isFavorite: Long, val notes: String?, val isCustom: Long,
    val createdAt: Long, val updatedAt: Long,
)

internal fun com.lifeforge.os.data.database.WorkoutProgram.toDomain(): WorkoutProgram = WorkoutProgram(
    id = id,
    name = name,
    nameAr = nameAr,
    description = description,
    type = type?.toEnumOr(ProgramType.Custom) ?: ProgramType.Custom,
    days = emptyList(),
    isActive = isActive == 1L,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun com.lifeforge.os.data.database.WorkoutDay.toDomain(exercises: List<WorkoutExercise> = emptyList()): WorkoutDay =
    WorkoutDay(
        id = id,
        programId = programId,
        dayIndex = dayIndex.toInt(),
        name = name,
        nameAr = nameAr,
        exercises = exercises,
        notes = notes,
    )

internal fun com.lifeforge.os.data.database.WorkoutExercise.toDomain(): WorkoutExercise = WorkoutExercise(
    id = id,
    dayId = dayId,
    exerciseId = exerciseId,
    order = exerciseOrder.toInt(),
    setsTarget = setsTarget?.toInt() ?: 3,
    repsTarget = repsTarget ?: "8-12",
    weightTarget = weightTarget,
    restSeconds = restSeconds?.toInt() ?: 90,
    notes = notes,
)

internal fun com.lifeforge.os.data.database.WorkoutSession.toDomain(sets: List<WorkoutSetResult> = emptyList()): WorkoutSession =
    WorkoutSession(
        id = id,
        programId = programId,
        dayId = dayId,
        name = name,
        startedAt = startedAt,
        completedAt = completedAt,
        durationSeconds = durationSeconds ?: 0,
        totalVolume = totalVolume ?: 0.0,
        exercises = emptyList(),
        isCompleted = isCompleted == 1L,
        notes = notes,
    )

internal fun com.lifeforge.os.data.database.WorkoutSet.toDomain(): WorkoutSetResult = WorkoutSetResult(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    setIndex = setIndex.toInt(),
    weight = weight ?: 0.0,
    reps = reps?.toInt() ?: 0,
    rpe = rpe,
    restSeconds = restSeconds?.toInt(),
    isCompleted = isCompleted == 1L,
    isWarmup = isWarmup == 1L,
)

// ============================================================
// Nutrition
// ============================================================

internal fun com.lifeforge.os.data.database.Food.toDomain(): Food = Food(
    id = id,
    name = name,
    nameAr = nameAr,
    servingSize = servingSize ?: "",
    facts = NutritionFacts(
        calories = calories ?: 0.0,
        protein = protein ?: 0.0,
        carbs = carbs ?: 0.0,
        fat = fat ?: 0.0,
        fiber = fiber ?: 0.0,
        sugar = sugar ?: 0.0,
        sodium = sodium ?: 0.0,
    ),
    isCustom = isCustom == 1L,
    isFavorite = isFavorite == 1L,
    notes = notes,
)

internal fun com.lifeforge.os.data.database.Meal.toDomain(items: List<MealItem> = emptyList()): Meal = Meal(
    id = id,
    date = date,
    type = type?.toEnumOr(MealType.Snack) ?: MealType.Snack,
    name = name,
    items = items,
    total = NutritionFacts(
        calories = totalCalories ?: 0.0,
        protein = totalProtein ?: 0.0,
        carbs = totalCarbs ?: 0.0,
        fat = totalFat ?: 0.0,
    ),
    notes = notes,
)

internal fun com.lifeforge.os.data.database.MealItem.toDomain(): MealItem = MealItem(
    id = id,
    mealId = mealId,
    foodId = foodId,
    foodName = foodId,
    facts = NutritionFacts(
        calories = calories ?: 0.0,
        protein = protein ?: 0.0,
        carbs = carbs ?: 0.0,
        fat = fat ?: 0.0,
    ),
)

internal fun com.lifeforge.os.data.database.WaterLog.toDomain(): WaterLog = WaterLog(
    id = id,
    date = date,
    time = time,
    amountMl = amountMl.toInt(),
)

// ============================================================
// Habits & Routines
// ============================================================

internal fun com.lifeforge.os.data.database.Habit.toDomain(): Habit = Habit(
    id = id,
    name = name,
    nameAr = nameAr,
    icon = icon,
    description = description,
    frequency = frequency?.toEnumOr(HabitFrequency.Daily) ?: HabitFrequency.Daily,
    targetValue = targetValue,
    targetUnit = targetUnit,
    isActive = isActive == 1L,
    startDate = startDate,
    currentStreak = currentStreak?.toInt() ?: 0,
    bestStreak = bestStreak?.toInt() ?: 0,
    totalCompletions = totalCompletions?.toInt() ?: 0,
)

internal fun com.lifeforge.os.data.database.Routine.toDomain(items: List<RoutineItem> = emptyList()): Routine = Routine(
    id = id,
    name = name,
    nameAr = nameAr,
    type = type?.toEnumOr(RoutineType.Custom) ?: RoutineType.Custom,
    items = items,
    isActive = isActive == 1L,
)

internal fun com.lifeforge.os.data.database.RoutineItem.toDomain(): RoutineItem = RoutineItem(
    id = id,
    routineId = routineId,
    name = name,
    time = time,
    durationMinutes = durationMinutes?.toInt(),
    order = itemOrder.toInt(),
    isCompleted = isCompleted == 1L,
)

// ============================================================
// Tasks
// ============================================================

internal fun com.lifeforge.os.data.database.Task.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    priority = priority?.toEnumOr(TaskPriority.Medium) ?: TaskPriority.Medium,
    category = category,
    isRecurring = isRecurring == 1L,
    isCompleted = isCompleted == 1L,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

// ============================================================
// Journal & Notes
// ============================================================

internal fun com.lifeforge.os.data.database.JournalEntry.toDomain(): JournalEntry = JournalEntry(
    id = id,
    date = date,
    title = title,
    content = content,
    mood = mood,
    energy = energy?.toInt(),
    stress = stress?.toInt(),
    tags = tags.asCsvList(),
    isPrivate = isPrivate == 1L,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun com.lifeforge.os.data.database.Note.toDomain(): Note = Note(
    id = id,
    type = type?.toEnumOr(NoteType.Quick) ?: NoteType.Quick,
    title = title,
    content = content,
    plainText = plainText,
    isPinned = isPinned == 1L,
    isPrivate = isPrivate == 1L,
    tags = tags.asCsvList(),
    linkedEntityType = linkedEntityType,
    linkedEntityId = linkedEntityId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

// ============================================================
// Books & Courses
// ============================================================

internal fun com.lifeforge.os.data.database.Book.toDomain(): Book = Book(
    id = id,
    title = title,
    author = author,
    coverUrl = coverUrl,
    description = description,
    category = category,
    tags = tags.asCsvList(),
    format = format,
    totalPages = totalPages?.toInt(),
    currentPage = currentPage?.toInt() ?: 0,
    status = status?.toEnumOr(BookStatus.WantToRead) ?: BookStatus.WantToRead,
    progressPercent = if (totalPages != null && totalPages > 0) (currentPage ?: 0) * 100.0 / totalPages else 0.0,
    rating = rating,
    isFavorite = isFavorite == 1L,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun com.lifeforge.os.data.database.Course.toDomain(
    sections: List<CourseSection> = emptyList(),
): Course = Course(
    id = id,
    title = title,
    provider = provider,
    instructor = instructor,
    thumbnailUrl = thumbnailUrl,
    description = description,
    category = category,
    sections = sections,
    status = com.lifeforge.os.domain.model.CourseStatus.entries.firstOrNull { it.name == status } ?: com.lifeforge.os.domain.model.CourseStatus.NotStarted,
    progressPercent = progress ?: 0.0,
    isFavorite = isFavorite == 1L,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun com.lifeforge.os.data.database.CourseSection.toDomain(lessons: List<CourseLesson> = emptyList()): CourseSection =
    CourseSection(
        id = id,
        courseId = courseId,
        title = title,
        order = sectionOrder.toInt(),
        lessons = lessons,
    )

internal fun com.lifeforge.os.data.database.CourseLesson.toDomain(): CourseLesson = CourseLesson(
    id = id,
    sectionId = sectionId,
    title = title,
    order = lessonOrder.toInt(),
    videoUrl = videoUrl,
    durationMinutes = durationMinutes?.toInt(),
    isCompleted = isCompleted == 1L,
    lastPositionSeconds = lastPosition?.toInt() ?: 0,
    progressPercent = progress?.toDouble() ?: 0.0,
)

// ============================================================
// Media
// ============================================================

internal fun com.lifeforge.os.data.database.MediaItem.toDomain(): MediaItem = MediaItem(
    id = id,
    type = type?.toEnumOr(MediaType.Video) ?: MediaType.Video,
    title = title,
    titleAr = titleAr,
    imageUrl = imageUrl,
    backdropUrl = backdropUrl,
    description = description,
    externalUrl = externalUrl,
    status = status?.toEnumOr(MediaStatus.WantToWatch) ?: MediaStatus.WantToWatch,
    rating = rating,
    personalRating = personalRating,
    review = review,
    tags = tags.asCsvList(),
    isFavorite = isFavorite == 1L,
    progressPercent = progressPercent ?: 0.0,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

// ============================================================
// Recovery & Timers
// ============================================================

internal fun com.lifeforge.os.data.database.RecoveryEntry.toDomain(): RecoveryEntry = RecoveryEntry(
    id = id,
    date = date,
    isRelapse = isRelapse == 1L,
    trigger = trigger,
    mood = mood,
    whatHappened = whatHappened,
    whatLearned = whatLearned,
    notes = notes,
    streakAtTime = streakAtTime?.toInt() ?: 0,
)

internal fun com.lifeforge.os.data.database.RecoveryTask.toDomain(): RecoveryTask = RecoveryTask(
    id = id,
    name = name,
    category = category,
    isActive = isActive == 1L,
)

internal fun com.lifeforge.os.data.database.TimerPreset.toDomain(): TimerPreset = TimerPreset(
    id = id,
    name = name,
    type = type?.toEnumOr(TimerType.Countdown) ?: TimerType.Countdown,
    phases = emptyList(),
    totalSeconds = totalDuration?.toInt() ?: 0,
    soundEnabled = soundEnabled == 1L,
    vibrationEnabled = vibrationEnabled == 1L,
)