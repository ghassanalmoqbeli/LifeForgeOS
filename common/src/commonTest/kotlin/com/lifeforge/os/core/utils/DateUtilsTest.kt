package com.lifeforge.os.core.utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateUtilsTest {

    @Test
    fun startOfTodayIsRecentMidnight() {
        val start = startOfTodayMillis()
        val now = System.currentTimeMillis()
        assertTrue(start <= now)
        val elapsed = now - start
        assertTrue(elapsed >= 0)
        assertTrue(elapsed < 3 * 24 * 60 * 60 * 1000L)
    }

    @Test
    fun sameDayTrueForSameInstant() {
        val now = System.currentTimeMillis()
        assertTrue(isSameDay(now, now))
    }

    @Test
    fun sameDayFalseForDifferentDays() {
        val start = startOfTodayMillis()
        assertFalse(isSameDay(start, start - 1))
    }
}