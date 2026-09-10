package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.core.utils.startOfTodayMillis
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal fun createTestDb(): LifeForgeDb {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    com.lifeforge.os.data.database.LifeForgeDatabase.Schema.create(driver)
    return LifeForgeDb(com.lifeforge.os.data.database.LifeForgeDatabase(driver))
}

class WaterRepositoryImplTest {

    @Test
    fun addAndSumWater() = runTest {
        val repo = WaterRepositoryImpl(createTestDb())
        val date = startOfTodayMillis()
        repo.addWater(amountMl = 250, date = date, time = System.currentTimeMillis())
        repo.addWater(amountMl = 250, date = date, time = System.currentTimeMillis())
        assertEquals(500, repo.getTotalForDay(date))
    }

    @Test
    fun observeDayReturnsLoggedWater() = runTest {
        val repo = WaterRepositoryImpl(createTestDb())
        val date = startOfTodayMillis()
        repo.addWater(amountMl = 350, date = date, time = System.currentTimeMillis())
        val logs = repo.observeDay(date).first()
        assertEquals(1, logs.size)
        assertEquals(350, logs.first().amountMl)
    }

    @Test
    fun removeWaterDeletesEntry() = runTest {
        val repo = WaterRepositoryImpl(createTestDb())
        val date = startOfTodayMillis()
        repo.addWater(amountMl = 150, date = date, time = System.currentTimeMillis())
        val logs = repo.observeDay(date).first()
        repo.removeWater(logs.first().id)
        assertEquals(0, repo.observeDay(date).first().size)
        assertEquals(0, repo.getTotalForDay(date))
    }

    @Test
    fun rangeFiltersByDate() = runTest {
        val repo = WaterRepositoryImpl(createTestDb())
        val first = startOfTodayMillis()
        val next = first + 24 * 60 * 60 * 1000L
        val far = first + 5 * 24 * 60 * 60 * 1000L
        repo.addWater(100, first, first)
        repo.addWater(200, next, next)
        val inRange = repo.observeRange(start = first, end = next).first()
        assertEquals(2, inRange.size)
        val single = repo.observeRange(start = first, end = first).first()
        assertEquals(1, single.size)
        assertEquals(100, single.first().amountMl)
        val none = repo.observeRange(start = far, end = far).first()
        assertEquals(0, none.size)
    }
}