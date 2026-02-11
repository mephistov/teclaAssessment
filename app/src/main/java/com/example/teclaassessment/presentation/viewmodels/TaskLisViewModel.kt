package com.example.teclaassessment.presentation.viewmodels

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.usecase.DeleteTaskUseCase
import com.example.teclaassessment.domain.usecase.GetAllTasksUseCase
import com.example.teclaassessment.domain.usecase.InsertTaskUseCase
import com.example.teclaassessment.domain.usecase.UpdateTaskUseCase
import com.example.teclaassessment.domain.models.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI State for task operations
data class TaskUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val insertTaskUseCase: InsertTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    // Task list from database
    val allTasks: StateFlow<List<TaskModel>> = getAllTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI state for operations (loading, errors, success)
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // Insert task with validation and network simulation
    fun insertTask(title: String, description: String) {
        viewModelScope.launch {
            // Start loading
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Call use case with validation
            when (val result = insertTaskUseCase(title, description)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Task added successfully!",
                            errorMessage = null
                        )
                    }
                    // Clear success message after 2 seconds
                    kotlinx.coroutines.delay(2000)
                    clearSuccessMessage()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            successMessage = null
                        )
                    }
                }
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun updateTask(task: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(task)
        }
    }

    fun toggleTaskCompletion(task: TaskModel) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            updateTaskUseCase(updatedTask)
        }
    }

    fun deleteTask(task: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    // Clear error message (called from UI)
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Clear success message
    private fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}