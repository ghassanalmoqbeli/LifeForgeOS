package com.lifeforge.os.data.repository

import com.lifeforge.os.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FoodRepositoryImplTest {

    @Test
    fun saveAndSearchFood() = runTest {
        val repo = FoodRepositoryImpl(createTestDb())
        val food = Food(
            id = "f1",
            name = "Chicken Breast",
            nameAr = "صدر دجاج",
            servingSize = "100g",
        )
        repo.save(food)
        assertEquals(1, repo.observeAll().first().size)
        val results = repo.search("chicken")
        assertEquals(1, results.size)
        assertEquals("صدر دجاج", results.first().nameAr)
        assertEquals(0, repo.search("rice").size)
    }

    @Test
    fun deleteRemovesFood() = runTest {
        val repo = FoodRepositoryImpl(createTestDb())
        repo.save(Food(id = "f1", name = "Apple", servingSize = "1 medium"))
        repo.delete("f1")
        assertEquals(0, repo.observeAll().first().size)
    }
}