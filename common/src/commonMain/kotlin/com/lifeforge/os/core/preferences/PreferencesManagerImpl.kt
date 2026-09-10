package com.lifeforge.os.core.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Shared preferences logic over a [KeyValueStore].
 */
class PreferencesManagerImpl(
    private val store: KeyValueStore,
) : PreferencesManager {

    // Theme
    override val themeMode: Flow<ThemeMode> = store.observeString("theme_mode", "System").map { ThemeMode.valueOf(it) }
    override val themePreset: Flow<ThemePreset> = store.observeString("theme_preset", "Midnight").map { ThemePreset.valueOf(it) }
    override val accentColor: Flow<Long> = store.observeLong("accent_color", 0x00BFA6L)
    override val fontScale: Flow<Float> = store.observeFloat("font_scale", 1f)
    override val density: Flow<Density> = store.observeString("density", "Normal").map { Density.valueOf(it) }
    override val cornerRadius: Flow<CornerRadius> = store.observeString("corner_radius", "Medium").map { CornerRadius.valueOf(it) }
    override val dashboardStyle: Flow<DashboardStyle> = store.observeString("dashboard_style", "Cards").map { DashboardStyle.valueOf(it) }

    // Profile
    override val userName: Flow<String?> = store.observeStringNullable("user_name", null)
    override val userAge: Flow<Int?> = store.observeInt("user_age", -1).map { it.takeIf { x -> x > 0 } }
    override val userHeight: Flow<String?> = store.observeStringNullable("user_height", null)
    override val userWeight: Flow<String?> = store.observeStringNullable("user_weight", null)
    override val userGoalWeight: Flow<String?> = store.observeStringNullable("user_goal_weight", null)
    override val units: Flow<Units> = store.observeString("units", "Metric").map { Units.valueOf(it) }
    override val caloriesGoal: Flow<Int> = store.observeInt("calories_goal", 2400)
    override val proteinGoal: Flow<Int> = store.observeInt("protein_goal", 180)
    override val waterGoal: Flow<Int> = store.observeInt("water_goal", 3000)

    // App Lock
    override val appLockEnabled: Flow<Boolean> = store.observeBoolean("app_lock_enabled", false)
    override val appLockPinHash: Flow<String?> = store.observeStringNullable("app_lock_pin_hash", null)
    override val appLockBiometricEnabled: Flow<Boolean> = store.observeBoolean("app_lock_biometric", true)
    override val appLockTimeout: Flow<Long> = store.observeLong("app_lock_timeout", 300_000L)
    override val appLockOnBackground: Flow<Boolean> = store.observeBoolean("app_lock_on_background", true)

    // Private Mode
    override val privateModeEnabled: Flow<Boolean> = store.observeBoolean("private_mode_enabled", false)
    override val hiddenSections: Flow<Set<String>> =
        store.observeStringNullable("hidden_sections", null).map { it?.split(",")?.filter(String::isNotBlank)?.toSet() ?: emptySet() }

    // Sync
    override val syncEnabled: Flow<Boolean> = store.observeBoolean("sync_enabled", false)
    override val lastSyncTime: Flow<Long?> = store.observeLong("last_sync_time", -1L).map { it.takeIf { x -> x > 0 } }
    override val syncOnlyWifi: Flow<Boolean> = store.observeBoolean("sync_only_wifi", true)

    // Backup
    override val autoBackup: Flow<Boolean> = store.observeBoolean("auto_backup", false)
    override val backupFrequency: Flow<BackupFrequency> = store.observeString("backup_frequency", "Daily").map { BackupFrequency.valueOf(it) }
    override val lastBackupTime: Flow<Long?> = store.observeLong("last_backup_time", -1L).map { it.takeIf { x -> x > 0 } }

    // Notifications
    override val notificationsEnabled: Flow<Boolean> = store.observeBoolean("notifications_enabled", true)
    override val workoutReminders: Flow<Boolean> = store.observeBoolean("workout_reminders", true)
    override val mealReminders: Flow<Boolean> = store.observeBoolean("meal_reminders", true)
    override val waterReminders: Flow<Boolean> = store.observeBoolean("water_reminders", true)
    override val habitReminders: Flow<Boolean> = store.observeBoolean("habit_reminders", true)
    override val recoveryReminders: Flow<Boolean> = store.observeBoolean("recovery_reminders", false)

    // Language
    override val language: Flow<String> = store.observeString("language", "ar")

    // Dashboard
    override val dashboardLayout: Flow<String?> = store.observeStringNullable("dashboard_layout", null)
    override val dashboardWidgets: Flow<String?> = store.observeStringNullable("dashboard_widgets", null)

    // Onboarding
    override val onboardingCompleted: Flow<Boolean> = store.observeBoolean("onboarding_completed", false)
    override val onboardingStep: Flow<Int> = store.observeInt("onboarding_step", 0)

    // Feature flags
    override val featureRecovery: Flow<Boolean> = store.observeBoolean("feature_recovery", false)
    override val featureCloudSync: Flow<Boolean> = store.observeBoolean("feature_cloud_sync", false)
    override val featureWindowsWidgets: Flow<Boolean> = store.observeBoolean("feature_windows_widgets", false)
    override val featureAdvancedReader: Flow<Boolean> = store.observeBoolean("feature_advanced_reader", false)

    // Versions
    override val appVersion: Flow<Int> = store.observeInt("app_version", 1)
    override val databaseVersion: Flow<Int> = store.observeInt("database_version", 1)

    override suspend fun setThemeMode(mode: ThemeMode) = store.putString("theme_mode", mode.name)
    override suspend fun setThemePreset(preset: ThemePreset) = store.putString("theme_preset", preset.name)
    override suspend fun setAccentColor(color: Long) = store.putLong("accent_color", color)
    override suspend fun setFontScale(scale: Float) = store.putFloat("font_scale", scale)
    override suspend fun setDensity(density: Density) = store.putString("density", density.name)
    override suspend fun setCornerRadius(radius: CornerRadius) = store.putString("corner_radius", radius.name)
    override suspend fun setDashboardStyle(style: DashboardStyle) = store.putString("dashboard_style", style.name)

    override suspend fun setUserProfile(profile: UserProfile) {
        store.putString("user_name", profile.name)
        store.putInt("user_age", profile.age ?: -1)
        profile.height?.let { store.putString("user_height", it) }
        profile.weight?.let { store.putString("user_weight", it) }
        profile.goalWeight?.let { store.putString("user_goal_weight", it) }
    }

    override suspend fun setUnits(units: Units) = store.putString("units", units.name)
    override suspend fun setCaloriesGoal(calories: Int) = store.putInt("calories_goal", calories)
    override suspend fun setProteinGoal(protein: Int) = store.putInt("protein_goal", protein)
    override suspend fun setWaterGoal(water: Int) = store.putInt("water_goal", water)

    override suspend fun setAppLockEnabled(enabled: Boolean) = store.putBoolean("app_lock_enabled", enabled)
    override suspend fun setAppLockPinHash(hash: String) = store.putString("app_lock_pin_hash", hash)
    override suspend fun setAppLockBiometricEnabled(enabled: Boolean) = store.putBoolean("app_lock_biometric", enabled)
    override suspend fun setAppLockTimeout(timeoutMs: Long) = store.putLong("app_lock_timeout", timeoutMs)
    override suspend fun setAppLockOnBackground(enabled: Boolean) = store.putBoolean("app_lock_on_background", enabled)

    override suspend fun setPrivateModeEnabled(enabled: Boolean) = store.putBoolean("private_mode_enabled", enabled)
    override suspend fun setHiddenSections(sections: Set<String>) = store.putString("hidden_sections", sections.joinToString(","))

    override suspend fun setSyncEnabled(enabled: Boolean) = store.putBoolean("sync_enabled", enabled)
    override suspend fun setLastSyncTime(time: Long) = store.putLong("last_sync_time", time)
    override suspend fun setSyncOnlyWifi(enabled: Boolean) = store.putBoolean("sync_only_wifi", enabled)

    override suspend fun setAutoBackup(enabled: Boolean) = store.putBoolean("auto_backup", enabled)
    override suspend fun setBackupFrequency(frequency: BackupFrequency) = store.putString("backup_frequency", frequency.name)
    override suspend fun setLastBackupTime(time: Long) = store.putLong("last_backup_time", time)

    override suspend fun setNotificationsEnabled(enabled: Boolean) = store.putBoolean("notifications_enabled", enabled)
    override suspend fun setWorkoutReminders(enabled: Boolean) = store.putBoolean("workout_reminders", enabled)
    override suspend fun setMealReminders(enabled: Boolean) = store.putBoolean("meal_reminders", enabled)
    override suspend fun setWaterReminders(enabled: Boolean) = store.putBoolean("water_reminders", enabled)
    override suspend fun setHabitReminders(enabled: Boolean) = store.putBoolean("habit_reminders", enabled)
    override suspend fun setRecoveryReminders(enabled: Boolean) = store.putBoolean("recovery_reminders", enabled)

    override suspend fun setLanguage(lang: String) = store.putString("language", lang)
    override suspend fun setDashboardLayout(layout: String) = store.putString("dashboard_layout", layout)
    override suspend fun setDashboardWidgets(widgets: String) = store.putString("dashboard_widgets", widgets)
    override suspend fun setOnboardingCompleted(completed: Boolean) = store.putBoolean("onboarding_completed", completed)
    override suspend fun setOnboardingStep(step: Int) = store.putInt("onboarding_step", step)
    override suspend fun setFeatureRecovery(enabled: Boolean) = store.putBoolean("feature_recovery", enabled)
    override suspend fun setFeatureCloudSync(enabled: Boolean) = store.putBoolean("feature_cloud_sync", enabled)
    override suspend fun setFeatureWindowsWidgets(enabled: Boolean) = store.putBoolean("feature_windows_widgets", enabled)
    override suspend fun setFeatureAdvancedReader(enabled: Boolean) = store.putBoolean("feature_advanced_reader", enabled)
    override suspend fun setAppVersion(version: Int) = store.putInt("app_version", version)
    override suspend fun setDatabaseVersion(version: Int) = store.putInt("database_version", version)
}