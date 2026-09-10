package com.lifeforge.os.core.preferences

import kotlinx.serialization.Serializable

/**
 * Sections in the app. Used for navigation visibility, home widget
 * visibility, and feature flags.
 */
enum class Section(
    val route: String,
    val label: String,
    val isPrivateByDefault: Boolean = false,
) {
    Home("home", "الرئيسية"),
    Gym("gym", "الصالة"),
    Nutrition("nutrition", "التغذية"),
    Habits("habits", "العادات"),
    Routine("routine", "الروتين"),
    Recovery("recovery", "التعافي", isPrivateByDefault = true),
    Books("books", "الكتب"),
    Courses("courses", "الدورات"),
    Entertainment("entertainment", "الترفيه"),
    Notes("notes", "الملاحظات"),
    Journal("journal", "المجلة"),
    Goals("goals", "الأهداف"),
    Timers("timers", "المؤقتات"),
    Statistics("statistics", "الإحصائيات"),
    Settings("settings", "الإعدادات"),
}

/**
 * Visibility state for each section.
 */
@Serializable
data class SectionVisibility(
    val section: Section,
    val isVisible: Boolean = true,
    val isPrivate: Boolean = false,
)

/**
 * Home dashboard widget types.
 */
enum class HomeWidgetType(
    val id: String,
    val label: String,
    val minSize: Int = 1,
    val maxSize: Int = 4,
) {
    TodayWorkout("today_workout", "تمرين اليوم", 1, 4),
    Calories("calories", "السعرات", 1, 2),
    Protein("protein", "البروتين", 1, 2),
    Water("water", "الماء", 1, 2),
    Weight("weight", "الوزن", 1, 2),
    HabitProgress("habit_progress", "تقدم العادات", 2, 4),
    RoutineProgress("routine_progress", "تقدم الروتين", 2, 4),
    Tasks("tasks", "المهام", 2, 4),
    Reading("reading", "القراءة", 1, 2),
    Courses("courses", "الدورات", 1, 2),
    Goals("goals", "الأهداف", 2, 4),
    Journal("journal", "المجلة", 1, 2),
    Recovery("recovery", "التعافي", 2, 4),
    Timeline("timeline", "المخطط الزمني", 2, 4),
    Continue("continue", "استمر", 2, 4),
    QuickActions("quick_actions", "إجراءات سريعة", 1, 2),
}

/**
 * Dashboard layout configuration for home screen.
 */
@Serializable
data class DashboardLayout(
    val widgetOrder: List<String> = emptyList(),
    val widgetSizes: Map<String, Int> = emptyMap(),
    val visibleSections: Set<Section> = Section.entries.toSet(),
)