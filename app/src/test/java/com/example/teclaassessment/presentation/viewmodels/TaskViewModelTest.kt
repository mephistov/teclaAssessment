package com.example.teclaassessment.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.usecase.DeleteTaskUseCase
import com.example.teclaassessment.domain.usecase.GetAllTasksUseCase
import com.example.teclaassessment.domain.usecase.InsertTaskUseCase
import com.example.teclaassessment.domain.usecase.UpdateTaskUseCase
import com.example.teclaassessment.domain.models.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    // Rule to run LiveData synchronously
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllTasksUseCase: GetAllTasksUseCase
    private lateinit var insertTaskUseCase: InsertTaskUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        // Set test dispatcher as main
        Dispatchers.setMain(testDispatcher)

        // Create mock use cases
        getAllTasksUseCase = mockk()
        insertTaskUseCase = mockk()
        updateTaskUseCase = mockk(relaxed = true)
        deleteTaskUseCase = mockk(relaxed = true)

        // Setup default behavior for getAllTasksUseCase
        every { getAllTasksUseCase() } returns flowOf(emptyList())

        // Create ViewModel
        viewModel = TaskViewModel(
            getAllTasksUseCase,
            insertTaskUseCase,
            updateTaskUseCase,
            deleteTaskUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `allTasks emits task list from use case`() = runTest {
        // Given
        val tasks = listOf(
            TaskModel(1, "Task 1", "Description 1", false),
            TaskModel(2, "Task 2", "Description 2", true)
        )
        every { getAllTasksUseCase() } returns flowOf(tasks)

        // Recreate ViewModel with new use case
        viewModel = TaskViewModel(
            getAllTasksUseCase,
            insertTaskUseCase,
            updateTaskUseCase,
            deleteTaskUseCase
        )

        // When/Then
        viewModel.allTasks.test {
            // Skip the initial empty emission from stateIn
            awaitItem() // First emission: initialValue = emptyList()

            // Now get the actual data
            val emission = awaitItem() // Second emission: actual tasks
            assertEquals(2, emission.size)
            assertEquals("Task 1", emission[0].title)
            assertEquals("Task 2", emission[1].title)
        }
    }

    @Test
    fun `insertTask with invalid data shows error state`() = runTest {
        // Given
        val title = "a".repeat(51) // Exceeds max length
        val description = "Description"
        val errorMessage = "Title cannot exceed 50 characters"
        coEvery {
            insertTaskUseCase(title, description)
        } returns Result.Error(errorMessage)

        // When
        viewModel.insertTask(title, description)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.errorMessage)
            assertNull(state.successMessage)
        }
    }

    @Test
    fun `insertTask shows loading state during operation`() = runTest {
        // Given - Simulate delay to keep loading state
        coEvery {
            insertTaskUseCase(any(), any())
        } coAnswers {
            delay(200) // Simulate longer operation
            Result.Success(Unit)
        }

        // Start collecting state BEFORE calling insertTask
        val states = mutableListOf<TaskUiState>()
        val job = launch {
            viewModel.uiState.collect { states.add(it) }
        }

        // When
        viewModel.insertTask("Title", "Description")

        // Advance time partially to capture loading state
        advanceTimeBy(50) // Don't complete the operation yet

        // Then - Check that we captured loading state
        assertTrue("Should have loading state", states.any { it.isLoading })

        // Complete the operation
        advanceUntilIdle()

        // Verify final state is not loading
        assertFalse("Should finish loading", viewModel.uiState.value.isLoading)

        job.cancel()
    }

    @Test
    fun `toggleTaskCompletion calls update use case with toggled task`() = runTest {
        // Given
        val task = TaskModel(1, "Task", "Description", false)
        val expectedUpdatedTask = task.copy(isCompleted = true)

        // When
        viewModel.toggleTaskCompletion(task)
        advanceUntilIdle()

        // Then
        coVerify { updateTaskUseCase(expectedUpdatedTask) }
    }

    @Test
    fun `deleteTask calls delete use case`() = runTest {
        // Given
        val task = TaskModel(1, "Task to delete", "Description", false)

        // When
        viewModel.deleteTask(task)
        advanceUntilIdle()

        // Then
        coVerify { deleteTaskUseCase(task) }
    }

    @Test
    fun `clearErrorMessage clears error from state`() = runTest {
        // Given - Set an error first
        coEvery {
            insertTaskUseCase(any(), any())
        } returns Result.Error("Some error")
        viewModel.insertTask("Title", "Description")
        advanceUntilIdle()

        // When
        viewModel.clearErrorMessage()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `updateTask calls update use case with correct task`() = runTest {
        // Given
        val task = TaskModel(1, "Updated", "New description", true)

        // When
        viewModel.updateTask(task)
        advanceUntilIdle()

        // Then
        coVerify { updateTaskUseCase(task) }
    }

    @Test
    fun `multiple insertTask calls handle state correctly`() = runTest {
        // Given
        coEvery {
            insertTaskUseCase(any(), any())
        } returns Result.Success(Unit)

        // When - Insert multiple tasks
        viewModel.insertTask("Task 1", "Desc 1")
        advanceUntilIdle()
        viewModel.insertTask("Task 2", "Desc 2")
        advanceUntilIdle()

        // Then - Verify both calls were made
        coVerify(exactly = 2) { insertTaskUseCase(any(), any()) }
    }
}
