package com.lifeforge.os.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.lifeforge.os.presentation.localization.L10n

enum class FeatureRoute {
    Nutrition,
    Gym,
    Habits,
    Tasks,
    Journal,
    Recovery,
    Timers,
    Goals,
    Routines,
    Notes,
    Books,
    Courses,
    Entertainment,
}

@Composable
fun FeatureRoute.label(strings: L10n): String = when (this) {
    FeatureRoute.Nutrition -> strings.nutrition
    FeatureRoute.Gym -> strings.gym
    FeatureRoute.Habits -> strings.habits
    FeatureRoute.Tasks -> strings.tasks
    FeatureRoute.Journal -> strings.journal
    FeatureRoute.Recovery -> strings.recovery
    FeatureRoute.Timers -> strings.timers
    FeatureRoute.Goals -> strings.goals
    FeatureRoute.Routines -> strings.routines
    FeatureRoute.Notes -> strings.notes
    FeatureRoute.Books -> strings.books
    FeatureRoute.Courses -> strings.courses
    FeatureRoute.Entertainment -> strings.entertainment
}

@Composable
fun FeatureRoute.icon(): ImageVector = when (this) {
    FeatureRoute.Nutrition -> Icons.Filled.Restaurant
    FeatureRoute.Gym -> Icons.Filled.FitnessCenter
    FeatureRoute.Habits -> Icons.Filled.Star
    FeatureRoute.Tasks -> Icons.Filled.List
    FeatureRoute.Journal -> Icons.Filled.Edit
    FeatureRoute.Recovery -> Icons.Filled.Spa
    FeatureRoute.Timers -> Icons.Filled.Timer
    FeatureRoute.Goals -> Icons.Filled.Flag
    FeatureRoute.Routines -> Icons.Filled.Schedule
    FeatureRoute.Notes -> Icons.Filled.DateRange
    FeatureRoute.Books -> Icons.Filled.Book
    FeatureRoute.Courses -> Icons.Filled.School
    FeatureRoute.Entertainment -> Icons.Filled.Favorite
}

val allFeatureRoutes: List<FeatureRoute> = listOf(
    FeatureRoute.Nutrition,
    FeatureRoute.Gym,
    FeatureRoute.Habits,
    FeatureRoute.Tasks,
    FeatureRoute.Journal,
    FeatureRoute.Recovery,
    FeatureRoute.Timers,
    FeatureRoute.Goals,
    FeatureRoute.Routines,
    FeatureRoute.Notes,
    FeatureRoute.Books,
    FeatureRoute.Courses,
    FeatureRoute.Entertainment,
)