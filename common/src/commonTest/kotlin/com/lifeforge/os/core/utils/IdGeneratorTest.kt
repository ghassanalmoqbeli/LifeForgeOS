package com.lifeforge.os.core.utils

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class IdGeneratorTest {

    @Test
    fun generatedIdsAreUniqueWithinRunLoop() {
        val ids = (1..100).map { newEntityId() }
        assertEqualsDistinct(ids)
    }

    @Test
    fun generatedIdIsNonBlankAndTrimmed() {
        val id = newEntityId()
        assertTrue(id.isNotBlank())
        assertTrue(id.length >= 10)
    }

    private fun assertEqualsDistinct(ids: List<String>) {
        val set = ids.toSet()
        assertTrue(set.size == ids.size, "duplicate ids found")
        assertNotEquals(ids.firstOrNull(), null)
    }
}