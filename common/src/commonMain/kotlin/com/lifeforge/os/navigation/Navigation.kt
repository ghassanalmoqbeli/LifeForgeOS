package com.lifeforge.os.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.lifeforge.os.core.preferences.Section
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ============================================================
// NAVIGATION ROUTES
// ============================================================

@Serializable
sealed interface AppDestination {
    @Serializable
    data object Onboarding : AppDestination

    @Serializable
    data object Home : AppDestination

    @Serializable
    data object Settings : AppDestination

    @Serializable
    data object Overview : AppDestination

    @Serializable
    data object Gym : AppDestination

    @Serializable
    data object GymDashboard : AppDestination

    @Serializable
    data object Exercises : AppDestination

    @Serializable
    data object Programs : AppDestination

    @Serializable
    data class ExerciseDetail(val exerciseId: String) : AppDestination

    @Serializable
    data class WorkoutProgramDetail(val programId: String) : AppDestination

    @Serializable
    data object ActiveWorkout : AppDestination

    @Serializable
    data class WorkoutSessionDetail(val sessionId: String) : AppDestination

    @Serializable
    data object WorkoutHistory : AppDestination

    @Serializable
    data object FitnessProgress : AppDestination

    @Serializable
    data object WorkoutRecords : AppDestination

    @Serializable
    data object BodyTracking : AppDestination

    @Serializable
    data object Nutrition : AppDestination

    @Serializable
    data object NutritionDashboard : AppDestination

    @Serializable
    data object Meals : AppDestination

    @Serializable
    data object Foods : AppDestination

    @Serializable
    data object NutritionHistory : AppDestination

    @Serializable
    data object Water : AppDestination

    @Serializable
    data object HabitList : AppDestination

    @Serializable
    data object RoutineList : AppDestination

    @Serializable
    data object Recovery : AppDestination

    @Serializable
    data object RecoveryDashboard : AppDestination

    @Serializable
    data object RecoveryCounter : AppDestination

    @Serializable
    data object RecoveryTasks : AppDestination

    @Serializable
    data object RecoveryJournal : AppDestination

    @Serializable
    data object RecoveryNotes : AppDestination

    @Serializable
    data object RecoveryWatchlist : AppDestination

    @Serializable
    data object RecoveryStatistics : AppDestination

    @Serializable
    data object Books : AppDestination

    @Serializable
    data object Library : AppDestination

    @Serializable
    data class BookDetail(val bookId: String) : AppDestination

    @Serializable
    data object ReadingStatistics : AppDestination

    @Serializable
    data object Courses : AppDestination

    @Serializable
    data class CourseDetail(val courseId: String) : AppDestination

    @Serializable
    data object CourseStatistics : AppDestination

    @Serializable
    data object Entertainment : AppDestination

    @Serializable
    data object EntertainmentHome : AppDestination

    @Serializable
    data object Movies : AppDestination

    @Serializable
    data object TVShows : AppDestination

    @Serializable
    data object Anime : AppDestination

    @Serializable
    data object Games : AppDestination

    @Serializable
    data object Videos : AppDestination

    @Serializable
    data object Podcasts : AppDestination

    @Serializable
    data object Music : AppDestination

    @Serializable
    data object Watchlists : AppDestination

    @Serializable
    data object Collections : AppDestination

    @Serializable
    data object MediaHistory : AppDestination

    @Serializable
    data object EntertainmentStatistics : AppDestination

    @Serializable
    data class MediaDetail(val mediaType: String, val mediaId: String) : AppDestination

    @Serializable
    data object Notes : AppDestination

    @Serializable
    data object NoteList : AppDestination

    @Serializable
    data class NoteEditor(val noteId: String? = null) : AppDestination

    @Serializable
    data object Journal : AppDestination

    @Serializable
    data class JournalEntryDetail(val entryId: String) : AppDestination

    @Serializable
    data object Goals : AppDestination

    @Serializable
    data object Timers : AppDestination

    @Serializable
    data object Statistics : AppDestination

    @Serializable
    data object OverviewStatistics : AppDestination

    @Serializable
    data object Calendar : AppDestination

    @Serializable
    data object Search : AppDestination

    @Serializable
    data object QuickAdd : AppDestination

    @Serializable
    data object AppLock : AppDestination

    @Serializable
    data object CreateDashboard : AppDestination
}

// Route strings for backward compat with widget deep links
object Routes {
    const val WORKOUT = "open/workout"
    const val READING = "open/reading/{bookId}"
    const val COURSE = "open/course/{courseId}"
    const val HABIT = "open/habit/{habitId}"
    const val TASK = "open/task/{taskId}"
}

// ============================================================
// BOTTOM NAVIGATION (Android)
// ============================================================

enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: String,
) {
    Home("home", "الرئيسية", "home"),
    Gym("gym", "الصالة", "gym"),
    Nutrition("nutrition", "التغذية", "nutrition"),
    Recovery("recovery", "التعافي", "recovery"),
    Settings("settings", "الإعدادات", "settings"),
}

object SectionRoutes {
    val Home = AppDestination.Home
    val Gym = AppDestination.Gym
    val Nutrition = AppDestination.Nutrition
    val Habits = AppDestination.HabitList
    val Routine = AppDestination.RoutineList
    val Recovery = AppDestination.Recovery
    val Books = AppDestination.Books
    val Courses = AppDestination.Courses
    val Entertainment = AppDestination.Entertainment
    val Notes = AppDestination.Notes
    val Journal = AppDestination.Journal
    val Goals = AppDestination.Goals
    val Timers = AppDestination.Timers
    val Statistics = AppDestination.Statistics
    val Settings = AppDestination.Settings
}

// Map Section -> Route for navigation
fun Section.toRoute(): AppDestination = when (this) {
    Section.Home -> AppDestination.Home
    Section.Gym -> AppDestination.Gym
    Section.Nutrition -> AppDestination.Nutrition
    Section.Habits -> AppDestination.HabitList
    Section.Routine -> AppDestination.RoutineList
    Section.Recovery -> AppDestination.Recovery
    Section.Books -> AppDestination.Books
    Section.Courses -> AppDestination.Courses
    Section.Entertainment -> AppDestination.Entertainment
    Section.Notes -> AppDestination.Notes
    Section.Journal -> AppDestination.Journal
    Section.Goals -> AppDestination.Goals
    Section.Timers -> AppDestination.Timers
    Section.Statistics -> AppDestination.Statistics
    Section.Settings -> AppDestination.Settings
}

// ============================================================
// NAVIGATION HELPERS
// ============================================================

data class LifeForgeNavigationState(
    val currentDestination: AppDestination?,
    val isDrawerOpen: Boolean,
)

fun NavController.navigateTo(destination: AppDestination) {
    navigate(destination) {
        // Default navigation options
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateToRoot() {
    popBackStack(AppDestination.Home, inclusive = false)
    navigate(AppDestination.Home) {
        launchSingleTop = true
    }
}

// Handle deep links for widgets
fun handleDeepLink(uri: String): AppDestination? {
    return when {
        uri.startsWith("lifeforge://open/workout") -> AppDestination.ActiveWorkout
        uri.startsWith("lifeforge://open/reading") -> {
            val bookId = uri.substringAfterLast("/")
            AppDestination.BookDetail(bookId)
        }
        uri.startsWith("lifeforge://open/course") -> {
            val courseId = uri.substringAfterLast("/")
            AppDestination.CourseDetail(courseId)
        }
        uri.startsWith("lifeforge://open/habit") -> {
            val habitId = uri.substringAfterLast("/")
            AppDestination.HabitList
        }
        uri.startsWith("lifeforge://open/task") -> {
            AppDestination.Home
        }
        else -> null
    }
}

// ============================================================
// NAV HOST - Platform-agnostic
// ============================================================

@Composable
fun LifeForgeNavHost(
    navController: NavHostController,
    startDestination: AppDestination,
    isOnboardingComplete: Boolean,
    onNavigate: (AppDestination) -> Unit,
    content: (AppDestination) -> Unit,
) {
    val actualStart = if (isOnboardingComplete) startDestination else AppDestination.Onboarding

    NavHost(
        navController = navController,
        startDestination = actualStart,
    ) {
        composable<AppDestination.Onboarding> {
            content(AppDestination.Onboarding)
        }
        composable<AppDestination.Home> {
            content(AppDestination.Home)
        }
        composable<AppDestination.Settings> {
            content(AppDestination.Settings)
        }
        composable<AppDestination.Gym> {
            content(AppDestination.Gym)
        }
        composable<AppDestination.Nutrition> {
            content(AppDestination.Nutrition)
        }
        composable<AppDestination.HabitList> {
            content(AppDestination.HabitList)
        }
        composable<AppDestination.RoutineList> {
            content(AppDestination.RoutineList)
        }
        composable<AppDestination.Recovery> {
            content(AppDestination.Recovery)
        }
        composable<AppDestination.Books> {
            content(AppDestination.Books)
        }
        composable<AppDestination.Courses> {
            content(AppDestination.Courses)
        }
        composable<AppDestination.Entertainment> {
            content(AppDestination.Entertainment)
        }
        composable<AppDestination.Notes> {
            content(AppDestination.Notes)
        }
        composable<AppDestination.Journal> {
            content(AppDestination.Journal)
        }
        composable<AppDestination.Goals> {
            content(AppDestination.Goals)
        }
        composable<AppDestination.Timers> {
            content(AppDestination.Timers)
        }
        composable<AppDestination.Statistics> {
            content(AppDestination.Statistics)
        }
        composable<AppDestination.Calendar> {
            content(AppDestination.Calendar)
        }
        composable<AppDestination.Search> {
            content(AppDestination.Search)
        }
        composable<AppDestination.QuickAdd> {
            content(AppDestination.QuickAdd)
        }

        // Detail routes
        composable<AppDestination.BookDetail>(
            deepLinks = listOf(navDeepLink { uriPattern = Routes.READING })
        ) { entry ->
            content(AppDestination.BookDetail(entry.safeArguments("bookId")))
        }
        composable<AppDestination.CourseDetail>(
            deepLinks = listOf(navDeepLink { uriPattern = Routes.COURSE })
        ) { entry ->
            content(AppDestination.CourseDetail(entry.safeArguments("courseId")))
        }
        composable<AppDestination.ExerciseDetail> { entry ->
            content(AppDestination.ExerciseDetail(entry.safeArguments("exerciseId")))
        }
        composable<AppDestination.WorkoutProgramDetail> { entry ->
            content(AppDestination.WorkoutProgramDetail(entry.safeArguments("programId")))
        }
        composable<AppDestination.WorkoutSessionDetail> { entry ->
            content(AppDestination.WorkoutSessionDetail(entry.safeArguments("sessionId")))
        }
        composable<AppDestination.MediaDetail> { entry ->
            content(AppDestination.MediaDetail(entry.safeArguments("mediaType"), entry.safeArguments("mediaId")))
        }
        composable<AppDestination.NoteEditor> { entry ->
            content(AppDestination.NoteEditor(entry.safeArgumentsNullable("noteId")))
        }
        composable<AppDestination.JournalEntryDetail> { entry ->
            content(AppDestination.JournalEntryDetail(entry.safeArguments("entryId")))
        }
    }
}

private fun NavBackStackEntry.safeArguments(key: String): String =
    arguments?.getString(key) ?: ""

private fun NavBackStackEntry.safeArgumentsNullable(key: String): String? =
    arguments?.getString(key)

// AppLock route handled separately by platform apps
object LockRoute {
    const val LOCK = "lock"
}