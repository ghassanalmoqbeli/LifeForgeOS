package com.lifeforge.os.android

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_withoutCrashing() {
        composeRule.activityRule.scenario.onActivity { activity ->
            assertTrue(activity != null)
        }
        composeRule.waitForIdle()
    }

    @Test
    fun composeRoot_rendersOnScreen() {
        composeRule.waitForIdle()
        composeRule.onRoot().assertExists()
        assertTrue(true)
    }
}