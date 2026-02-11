package com.example.teclaassessment.presentation.viewmodels

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.usecase.DeleteTaskUseCase
import com.example.teclaassessment.domain.usecase.GetAllTasksUseCase
import com.example.teclaassessment.domain.usecase.InsertTaskUseCase
import com.example.teclaassessment.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val insertTaskUseCase: InsertTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    val allTasks: StateFlow<List<TaskModel>> = getAllTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertTask(title: String, description: String) {
        viewModelScope.launch {
            val task = TaskModel(
                title = title,
                description = description
            )
            insertTaskUseCase(task)
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
}