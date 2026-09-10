package com.lifeforge.os.core.preferences

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesManagerImplTest {

    @Test
    fun waterGoalPersists() = runTest {
        val prefs = PreferencesManagerImpl(InMemoryKeyValueStore())
        assertEquals(2400, prefs.waterGoal.first())
        prefs.setWaterGoal(3000)
        assertEquals(3000, prefs.waterGoal.first())
    }

    @Test
    fun themeModeCycles() = runTest {
        val prefs = PreferencesManagerImpl(InMemoryKeyValueStore())
        assertEquals(ThemeMode.System, prefs.themeMode.first())
        prefs.setThemeMode(ThemeMode.Dark)
        assertEquals(ThemeMode.Dark, prefs.themeMode.first())
    }

    @Test
    fun onboardingCompletedPersists() = runTest {
        val prefs = PreferencesManagerImpl(InMemoryKeyValueStore())
        assertEquals(false, prefs.onboardingCompleted.first())
        prefs.setOnboardingCompleted(true)
        assertEquals(true, prefs.onboardingCompleted.first())
    }

    @Test
    fun userProfilePersists() = runTest {
        val prefs = PreferencesManagerImpl(InMemoryKeyValueStore())
        val profile = UserProfile(name = "عمر", age = 30, height = "178", weight = "80")
        prefs.setUserProfile(profile)
        assertEquals("عمر", prefs.userName.first())
        assertEquals(30, prefs.userAge.first())
    }

    @Test
    fun notificationsPersist() = runTest {
        val prefs = PreferencesManagerImpl(InMemoryKeyValueStore())
        prefs.setNotificationsEnabled(false)
        assertEquals(false, prefs.notificationsEnabled.first())
    }
}