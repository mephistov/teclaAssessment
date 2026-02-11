package com.example.teclaassessment.data.repository

import com.example.teclaassessment.data.local.TaskDao
import com.example.teclaassessment.data.local.TaskEntity
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.models.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TaskRepositoryImplTest {

    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepositoryImpl

    @Before
    fun setUp() {
        taskDao = mockk(relaxed = true)
        repository = TaskRepositoryImpl(taskDao)
    }

    @Test
    fun `getAllTasks returns flow of tasks from dao`() = runTest {
        // Given
        val entities = listOf(
            TaskEntity(1, "Task 1", "Description 1", false, 1000L),
            TaskEntity(2, "Task 2", "Description 2", true, 2000L)
        )
        coEvery { taskDao.getAllTasks() } returns flowOf(entities)

        // When
        val result = repository.getAllTasks().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Task 1", result[0].title)
        assertEquals("Task 2", result[1].title)
        assertEquals(false, result[0].isCompleted)
        assertEquals(true, result[1].isCompleted)
    }

    @Test
    fun `getAllTasks returns empty list when dao returns empty`() = runTest {
        // Given
        coEvery { taskDao.getAllTasks() } returns flowOf(emptyList())

        // When
        val result = repository.getAllTasks().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getTasksByStatus returns completed tasks only`() = runTest {
        // Given
        val completedEntities = listOf(
            TaskEntity(1, "Completed Task", "Done", true, 1000L)
        )
        coEvery { taskDao.getTasksByStatus(true) } returns flowOf(completedEntities)

        // When
        val result = repository.getTasksByStatus(true).first()

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isCompleted)
        coVerify { taskDao.getTasksByStatus(true) }
    }

    @Test
    fun `insertTask calls dao when successful`() = runTest {
        // Given
        val task = TaskModel(0, "New Task", "Description", false, 1000L)
        coEvery { taskDao.insertTask(any()) } returns Unit

        // When
        var attempts = 0
        var successResult: Result<Unit>? = null

        // Try multiple times to get a success (due to 75% success rate)
        while (successResult == null && attempts < 10) {
            val result = repository.insertTask(task)
            if (result is Result.Success) {
                successResult = result
            }
            attempts++
        }

        // Then
        assertNotNull("Should eventually succeed", successResult)
        coVerify(atLeast = 1) { taskDao.insertTask(any()) }
    }

    @Test
    fun `insertTask returns error when simulated network fails`() = runTest {
        // Given
        val task = TaskModel(0, "New Task", "Description", false, 1000L)

        // When - Run multiple times to eventually hit a failure
        var errorResult: Result.Error? = null
        var attempts = 0

        while (errorResult == null && attempts < 20) {
            val result = repository.insertTask(task)
            if (result is Result.Error) {
                errorResult = result
            }
            attempts++
        }

        // Then - Should eventually get an error (25% failure rate)
        assertNotNull("Should eventually fail", errorResult)
        assertTrue(errorResult!!.message.contains("Network error"))
    }

    @Test
    fun `updateTask calls dao with correct entity`() = runTest {
        // Given
        val task = TaskModel(1, "Updated Task", "Updated Description", true, 1000L)

        // When
        repository.updateTask(task)

        // Then
        coVerify { taskDao.updateTask(any()) }
    }

    @Test
    fun `deleteTask calls dao with correct entity`() = runTest {
        // Given
        val task = TaskModel(1, "Task to delete", "Description", false, 1000L)

        // When
        repository.deleteTask(task)

        // Then
        coVerify { taskDao.deleteTask(any()) }
    }

    @Test
    fun `deleteCompletedTasks calls dao`() = runTest {
        // When
        repository.deleteCompletedTasks()

        // Then
        coVerify { taskDao.deleteCompletedTasks() }
    }

    @Test
    fun `getTaskById returns null when task not found`() = runTest {
        // Given
        coEvery { taskDao.getTaskById(999) } returns null

        // When
        val result = repository.getTaskById(999)

        // Then
        assertNull(result)
    }

    @Test
    fun `getTaskById returns task when found`() = runTest {
        // Given
        val entity = TaskEntity(1, "Found Task", "Description", false, 1000L)
        coEvery { taskDao.getTaskById(1) } returns entity

        // When
        val result = repository.getTaskById(1)

        // Then
        assertNotNull(result)
        assertEquals("Found Task", result?.title)
    }

}