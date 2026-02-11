package com.example.teclaassessment.domain.usecase

import com.example.teclaassessment.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.example.teclaassessment.domain.models.Result
import io.mockk.coVerify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue


class InsertTaskUseCaseTest {

    private lateinit var repository: TaskRepository
    private lateinit var insertTaskUseCase: InsertTaskUseCase

    @Before
    fun setUp() {
        repository = mockk()
        insertTaskUseCase = InsertTaskUseCase(repository)
    }

    @Test
    fun `invoke with valid title and description returns success`() = runTest {
        // Given
        val title = "Valid Title"
        val description = "Valid description"
        coEvery { repository.insertTask(any()) } returns Result.Success(Unit)

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.insertTask(any()) }
    }

    @Test
    fun `invoke with title exceeding max length returns error`() = runTest {
        // Given - Title with 51 characters (max is 50)
        val title = "a".repeat(51)
        val description = "Valid description"

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(
            "Title cannot exceed ${InsertTaskUseCase.TITLE_MAX_LENGTH} characters",
            (result as Result.Error).message
        )
        coVerify(exactly = 0) { repository.insertTask(any()) }
    }

    @Test
    fun `invoke with description exceeding max length returns error`() = runTest {
        // Given - Description with 201 characters (max is 200)
        val title = "Valid Title"
        val description = "a".repeat(201)

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(
            "Description cannot exceed ${InsertTaskUseCase.DESCRIPTION_MAX_LENGTH} characters",
            (result as Result.Error).message
        )
        coVerify(exactly = 0) { repository.insertTask(any()) }
    }

    @Test
    fun `invoke with blank title returns error`() = runTest {
        // Given
        val title = "   "  // Blank title
        val description = "Valid description"

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Title cannot be empty", (result as Result.Error).message)
        coVerify(exactly = 0) { repository.insertTask(any()) }
    }

    @Test
    fun `invoke with empty title returns error`() = runTest {
        // Given
        val title = ""
        val description = "Valid description"

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.insertTask(any()) }
    }

    @Test
    fun `invoke with maximum allowed lengths succeeds`() = runTest {
        // Given - Exactly 50 chars for title, 200 for description
        val title = "a".repeat(50)
        val description = "b".repeat(200)
        coEvery { repository.insertTask(any()) } returns Result.Success(Unit)

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.insertTask(any()) }
    }

    @Test
    fun `invoke propagates repository error`() = runTest {
        // Given
        val title = "Valid Title"
        val description = "Valid description"
        val errorMessage = "Network error"
        coEvery { repository.insertTask(any()) } returns Result.Error(errorMessage)

        // When
        val result = insertTaskUseCase(title, description)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
    }

}