package com.lifeforge.os.presentation.settings

import com.lifeforge.os.core.preferences.PreferencesManager
import com.lifeforge.os.core.preferences.ThemeMode
import com.lifeforge.os.core.preferences.ThemePreset
import com.lifeforge.os.core.preferences.Units
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val scopeProvider: CoroutineScopeProvider,
) {
    val themeMode: StateFlow<ThemeMode> =
        preferencesManager.themeMode.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, ThemeMode.System)

    val themePreset: StateFlow<ThemePreset> =
        preferencesManager.themePreset.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, ThemePreset.Midnight)

    val accentColor: StateFlow<Long> =
        preferencesManager.accentColor.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, 0x00BFA6L)

    val units: StateFlow<Units> =
        preferencesManager.units.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, Units.Metric)

    val waterGoal: StateFlow<Int> =
        preferencesManager.waterGoal.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, 2500)

    val language: StateFlow<String> =
        preferencesManager.language.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, "ar")

    val notificationsEnabled: StateFlow<Boolean> =
        preferencesManager.notificationsEnabled.stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, true)

    fun setThemeMode(mode: ThemeMode) {
        scopeProvider.ioScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun setThemePreset(preset: ThemePreset) {
        scopeProvider.ioScope.launch { preferencesManager.setThemePreset(preset) }
    }

    fun setUnits(units: Units) {
        scopeProvider.ioScope.launch { preferencesManager.setUnits(units) }
    }

    fun setWaterGoal(goal: Int) {
        scopeProvider.ioScope.launch { preferencesManager.setWaterGoal(goal.coerceIn(0, 10_000)) }
    }

    fun setLanguage(language: String) {
        scopeProvider.ioScope.launch { preferencesManager.setLanguage(language) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        scopeProvider.ioScope.launch { preferencesManager.setNotificationsEnabled(enabled) }
    }
}