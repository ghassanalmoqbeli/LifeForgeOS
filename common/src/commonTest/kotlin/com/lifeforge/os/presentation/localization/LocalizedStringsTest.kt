package com.lifeforge.os.presentation.localization

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LocalizedStringsTest {

    @Test
    fun arabicAndEnglishDiffer() {
        assertNotEquals(LocalizedStrings.en, LocalizedStrings.ar)
    }

    @Test
    fun bothLanguagesFillEveryKey() {
        val checks: Map<String, String> = mapOf(
            "home" to LocalizedStrings.en.home,
            "settings" to LocalizedStrings.ar.settings,
            "continueLabel" to LocalizedStrings.en.continueLabel,
            "waterToday" to LocalizedStrings.ar.waterToday,
            "addWater" to LocalizedStrings.en.addWater,
            "unitsLabel" to LocalizedStrings.ar.unitsLabel,
            "themeSystem" to LocalizedStrings.en.themeSystem,
        )
        assertTrue(checks.all { it.value.isNotBlank() }, "blank localized string found")
    }
}