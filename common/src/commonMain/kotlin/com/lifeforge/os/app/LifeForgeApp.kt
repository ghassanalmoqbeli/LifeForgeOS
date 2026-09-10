package com.lifeforge.os.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import com.lifeforge.os.designSystem.theme.LifeForgeTheme
import com.lifeforge.os.presentation.books.BooksViewModel
import com.lifeforge.os.presentation.components.AppTab
import com.lifeforge.os.presentation.components.LifeForgeScaffold
import com.lifeforge.os.presentation.components.label
import com.lifeforge.os.presentation.courses.CoursesViewModel
import com.lifeforge.os.presentation.entertainment.EntertainmentViewModel
import com.lifeforge.os.presentation.goals.GoalsViewModel
import com.lifeforge.os.presentation.gym.GymViewModel
import com.lifeforge.os.presentation.habits.HabitsViewModel
import com.lifeforge.os.presentation.home.HomeViewModel
import com.lifeforge.os.presentation.journal.JournalViewModel
import com.lifeforge.os.presentation.localization.LocalizedStrings
import com.lifeforge.os.presentation.navigation.FeatureRoute
import com.lifeforge.os.presentation.navigation.label
import com.lifeforge.os.presentation.notes.NotesViewModel
import com.lifeforge.os.presentation.nutrition.NutritionViewModel
import com.lifeforge.os.presentation.onboarding.OnboardingViewModel
import com.lifeforge.os.presentation.recovery.RecoveryViewModel
import com.lifeforge.os.presentation.routines.RoutinesViewModel
import com.lifeforge.os.presentation.screens.BooksScreen
import com.lifeforge.os.presentation.screens.CoursesScreen
import com.lifeforge.os.presentation.screens.EntertainmentScreen
import com.lifeforge.os.presentation.screens.GoalsScreen
import com.lifeforge.os.presentation.screens.GymScreen
import com.lifeforge.os.presentation.screens.HabitsScreen
import com.lifeforge.os.presentation.screens.HomeScreen
import com.lifeforge.os.presentation.screens.JournalScreen
import com.lifeforge.os.presentation.screens.NotesScreen
import com.lifeforge.os.presentation.screens.NutritionScreen
import com.lifeforge.os.presentation.screens.OnboardingScreen
import com.lifeforge.os.presentation.screens.RecoveryScreen
import com.lifeforge.os.presentation.screens.RoutinesScreen
import com.lifeforge.os.presentation.screens.SettingsScreen
import com.lifeforge.os.presentation.screens.TasksScreen
import com.lifeforge.os.presentation.screens.TimersScreen
import com.lifeforge.os.presentation.settings.SettingsViewModel
import com.lifeforge.os.presentation.tasks.TasksViewModel
import com.lifeforge.os.presentation.timers.TimersViewModel
import org.koin.compose.koinInject

/**
 * Shared root of the application UI across all platforms.
 */
@Composable
fun LifeForgeApp() {
    val settingsViewModel: SettingsViewModel = koinInject()
    val onboardingViewModel: OnboardingViewModel = koinInject()

    val language by settingsViewModel.language.collectAsState()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val themePreset by settingsViewModel.themePreset.collectAsState()
    val accentColor by settingsViewModel.accentColor.collectAsState()
    val isOnboarded by onboardingViewModel.isCompleted.collectAsState()

    val strings = remember(language) { LocalizedStrings.stringsFor(language) }
    val layoutDirection = remember(language) {
        if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    var tab by rememberSaveable { mutableStateOf(AppTab.Home) }
    var featureStack by remember { mutableStateOf<List<FeatureRoute>>(emptyList()) }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        LifeForgeTheme(
            themeMode = themeMode,
            themePreset = themePreset,
            accentColor = Color(0xFF000000L or accentColor),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    !isOnboarded -> OnboardingScreen(viewModel = onboardingViewModel, strings = strings)
                    else -> LifeForgeScaffold(
                        strings = strings,
                        currentTab = tab,
                        onTabSelected = { newTab ->
                            tab = newTab
                            featureStack = emptyList()
                        },
                        topTitle = featureStack.lastOrNull()?.label(strings) ?: tab.label(strings),
                        showBack = featureStack.isNotEmpty(),
                        onBack = {
                            featureStack = featureStack.dropLast(1)
                        },
                    ) {
                        val currentFeature = featureStack.lastOrNull()
                        if (currentFeature != null) {
                            FeatureScreen(route = currentFeature, strings = strings, onBack = {
                                featureStack = featureStack.dropLast(1)
                            })
                        } else {
                            when (tab) {
                                AppTab.Home -> HomeScreen(
                                    viewModel = koinInject<HomeViewModel>(),
                                    strings = strings,
                                    onFeatureSelected = { featureStack = featureStack + it },
                                )
                                AppTab.Settings -> SettingsScreen(viewModel = settingsViewModel, strings = strings)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureScreen(
    route: FeatureRoute,
    strings: com.lifeforge.os.presentation.localization.L10n,
    onBack: () -> Unit,
) {
    when (route) {
        FeatureRoute.Nutrition -> NutritionScreen(koinInject<NutritionViewModel>(), strings)
        FeatureRoute.Gym -> GymScreen(koinInject<GymViewModel>(), strings)
        FeatureRoute.Habits -> HabitsScreen(koinInject<HabitsViewModel>(), strings)
        FeatureRoute.Tasks -> TasksScreen(koinInject<TasksViewModel>(), strings)
        FeatureRoute.Journal -> JournalScreen(koinInject<JournalViewModel>(), strings)
        FeatureRoute.Recovery -> RecoveryScreen(koinInject<RecoveryViewModel>(), strings)
        FeatureRoute.Timers -> TimersScreen(koinInject<TimersViewModel>(), strings)
        FeatureRoute.Goals -> GoalsScreen(koinInject<GoalsViewModel>(), strings)
        FeatureRoute.Routines -> RoutinesScreen(koinInject<RoutinesViewModel>(), strings)
        FeatureRoute.Notes -> NotesScreen(koinInject<NotesViewModel>(), strings)
        FeatureRoute.Books -> BooksScreen(koinInject<BooksViewModel>(), strings)
        FeatureRoute.Courses -> CoursesScreen(koinInject<CoursesViewModel>(), strings)
        FeatureRoute.Entertainment -> EntertainmentScreen(koinInject<EntertainmentViewModel>(), strings)
    }
}