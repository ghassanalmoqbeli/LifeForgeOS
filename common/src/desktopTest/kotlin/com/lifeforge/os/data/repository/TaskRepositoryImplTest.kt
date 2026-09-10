package com.lifeforge.os.data.repository

import com.lifeforge.os.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskRepositoryImplTest {

    private fun sampleTask(id: String, title: String): Task {
        val now = System.currentTimeMillis()
        return Task(id = id, title = title, createdAt = now, updatedAt = now)
    }

    @Test
    fun saveAndReadTask() = runTest {
        val repo = TaskRepositoryImpl(createTestDb())
        repo.save(sampleTask("t1", "Drink water"))
        val task = repo.getById("t1")
        assertNotNull(task)
        assertEquals("Drink water", task.title)
    }

    @Test
    fun openAndCompletedViews() = runTest {
        val repo = TaskRepositoryImpl(createTestDb())
        repo.save(sampleTask("t1", "Open task"))
        assertEquals(1, repo.observeOpen().first().size)
        val toggled = repo.toggleCompleted("t1")
        assertTrue(toggled?.isCompleted == true)
        assertEquals(0, repo.observeOpen().first().size)
        assertEquals(1, repo.observeCompleted().first().size)
    }

    @Test
    fun deleteRemovesTask() = runTest {
        val repo = TaskRepositoryImpl(createTestDb())
        repo.save(sampleTask("t1", "Delete me"))
        repo.delete("t1")
        assertNull(repo.getById("t1"))
        assertEquals(0, repo.observeOpen().first().size)
    }

    @Test
    fun tasksFilteredByDate() = runTest {
        val repo = TaskRepositoryImpl(createTestDb())
        val today = System.currentTimeMillis()
        repo.save(sampleTask("t1", "Today task").copy(date = today))
        repo.save(sampleTask("t2", "Undated task"))
        assertEquals(1, repo.observeTasks(date = today).first().size)
        assertEquals(2, repo.observeTasks(date = null).first().size)
    }
}