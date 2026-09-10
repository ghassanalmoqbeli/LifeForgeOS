package com.lifeforge.os.core.preferences

import kotlinx.coroutines.flow.Flow

enum class ThemeMode { Light, Dark, System }
enum class ThemePreset { Midnight, Graphite, Ocean, Forest, Minimal, Custom }
enum class Density { Compact, Normal, Comfortable }
enum class CornerRadius { None, Small, Medium, Large, ExtraLarge }
enum class DashboardStyle { Cards, List, Compact, Hybrid }
enum class Units { Metric, Imperial }
enum class BackupFrequency { Daily, Weekly, Monthly, Manual }

data class UserProfile(
    val name: String,
    val age: Int? = null,
    val height: String? = null,
    val weight: String? = null,
    val goalWeight: String? = null,
)

/**
 * Cross-platform preferences store backed by platform key-value storage.
 */
interface PreferencesManager {
    // Theme
    val themeMode: Flow<ThemeMode>
    val themePreset: Flow<ThemePreset>
    val accentColor: Flow<Long>
    val fontScale: Flow<Float>
    val density: Flow<Density>
    val cornerRadius: Flow<CornerRadius>
    val dashboardStyle: Flow<DashboardStyle>

    // User Profile
    val userName: Flow<String?>
    val userAge: Flow<Int?>
    val userHeight: Flow<String?>
    val userWeight: Flow<String?>
    val userGoalWeight: Flow<String?>
    val units: Flow<Units>
    val caloriesGoal: Flow<Int>
    val proteinGoal: Flow<Int>
    val waterGoal: Flow<Int>

    // App Lock
    val appLockEnabled: Flow<Boolean>
    val appLockPinHash: Flow<String?>
    val appLockBiometricEnabled: Flow<Boolean>
    val appLockTimeout: Flow<Long>
    val appLockOnBackground: Flow<Boolean>

    // Private Mode
    val privateModeEnabled: Flow<Boolean>
    val hiddenSections: Flow<Set<String>>

    // Sync
    val syncEnabled: Flow<Boolean>
    val lastSyncTime: Flow<Long?>
    val syncOnlyWifi: Flow<Boolean>

    // Backup
    val autoBackup: Flow<Boolean>
    val backupFrequency: Flow<BackupFrequency>
    val lastBackupTime: Flow<Long?>

    // Notifications
    val notificationsEnabled: Flow<Boolean>
    val workoutReminders: Flow<Boolean>
    val mealReminders: Flow<Boolean>
    val waterReminders: Flow<Boolean>
    val habitReminders: Flow<Boolean>
    val recoveryReminders: Flow<Boolean>

    // Language
    val language: Flow<String>

    // Dashboard
    val dashboardLayout: Flow<String?>
    val dashboardWidgets: Flow<String?>

    // Onboarding
    val onboardingCompleted: Flow<Boolean>
    val onboardingStep: Flow<Int>

    // Feature Flags
    val featureRecovery: Flow<Boolean>
    val featureCloudSync: Flow<Boolean>
    val featureWindowsWidgets: Flow<Boolean>
    val featureAdvancedReader: Flow<Boolean>

    // Versions
    val appVersion: Flow<Int>
    val databaseVersion: Flow<Int>

    // Theme setters
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setThemePreset(preset: ThemePreset)
    suspend fun setAccentColor(color: Long)
    suspend fun setFontScale(scale: Float)
    suspend fun setDensity(density: Density)
    suspend fun setCornerRadius(radius: CornerRadius)
    suspend fun setDashboardStyle(style: DashboardStyle)

    // Profile setters
    suspend fun setUserProfile(profile: UserProfile)
    suspend fun setUnits(units: Units)
    suspend fun setCaloriesGoal(calories: Int)
    suspend fun setProteinGoal(protein: Int)
    suspend fun setWaterGoal(water: Int)

    // Lock setters
    suspend fun setAppLockEnabled(enabled: Boolean)
    suspend fun setAppLockPinHash(hash: String)
    suspend fun setAppLockBiometricEnabled(enabled: Boolean)
    suspend fun setAppLockTimeout(timeoutMs: Long)
    suspend fun setAppLockOnBackground(enabled: Boolean)

    // Privacy setters
    suspend fun setPrivateModeEnabled(enabled: Boolean)
    suspend fun setHiddenSections(sections: Set<String>)

    // Sync setters
    suspend fun setSyncEnabled(enabled: Boolean)
    suspend fun setLastSyncTime(time: Long)
    suspend fun setSyncOnlyWifi(enabled: Boolean)

    // Backup setters
    suspend fun setAutoBackup(enabled: Boolean)
    suspend fun setBackupFrequency(frequency: BackupFrequency)
    suspend fun setLastBackupTime(time: Long)

    // Notification setters
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setWorkoutReminders(enabled: Boolean)
    suspend fun setMealReminders(enabled: Boolean)
    suspend fun setWaterReminders(enabled: Boolean)
    suspend fun setHabitReminders(enabled: Boolean)
    suspend fun setRecoveryReminders(enabled: Boolean)

    // Other setters
    suspend fun setLanguage(lang: String)
    suspend fun setDashboardLayout(layout: String)
    suspend fun setDashboardWidgets(widgets: String)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setOnboardingStep(step: Int)
    suspend fun setFeatureRecovery(enabled: Boolean)
    suspend fun setFeatureCloudSync(enabled: Boolean)
    suspend fun setFeatureWindowsWidgets(enabled: Boolean)
    suspend fun setFeatureAdvancedReader(enabled: Boolean)
    suspend fun setAppVersion(version: Int)
    suspend fun setDatabaseVersion(version: Int)
}