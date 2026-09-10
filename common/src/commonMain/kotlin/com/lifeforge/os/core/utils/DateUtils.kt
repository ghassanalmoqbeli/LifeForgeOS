package com.lifeforge.os.core.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun startOfTodayMillis(): Long {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return now.date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun isSameDay(first: Long, second: Long): Boolean {
    val tz = TimeZone.currentSystemDefault()
    val a = kotlinx.datetime.Instant.fromEpochMilliseconds(first).toLocalDateTime(tz).date
    val b = kotlinx.datetime.Instant.fromEpochMilliseconds(second).toLocalDateTime(tz).date
    return a == b
}