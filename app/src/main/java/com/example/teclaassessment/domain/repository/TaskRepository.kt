package com.example.teclaassessment.domain.repository

import com.example.teclaassessment.domain.models.Result
import com.example.teclaassessment.domain.models.TaskModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskModel>>
    fun getTasksByStatus(isCompleted: Boolean): Flow<List<TaskModel>>
    suspend fun getTaskById(taskId: Int): TaskModel?

    // Modified to return Result with network simulation
    suspend fun insertTask(task: TaskModel): Result<Unit>

    suspend fun updateTask(task: TaskModel)
    suspend fun deleteTask(task: TaskModel)
    suspend fun deleteCompletedTasks()
}