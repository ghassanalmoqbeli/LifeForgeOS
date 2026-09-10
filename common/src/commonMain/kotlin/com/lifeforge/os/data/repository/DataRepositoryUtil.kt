package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.data.database.LifeForgeDatabaseQueries

internal val LifeForgeDb.q: LifeForgeDatabaseQueries
    get() = database.lifeForgeDatabaseQueries

internal fun Long?.orZero(): Long = this ?: 0L

internal fun generateId(): String =
    "${System.currentTimeMillis()}-${kotlin.random.Random.nextLong().toString().substring(1, 9)}"