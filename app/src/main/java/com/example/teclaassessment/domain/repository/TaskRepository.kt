package com.example.teclaassessment.domain.repository

import com.example.teclaassessment.domain.models.TaskModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskModel>>
    fun getTasksByStatus(isCompleted: Boolean): Flow<List<TaskModel>>
    suspend fun getTaskById(taskId: Int): TaskModel?
    suspend fun insertTask(task: TaskModel)
    suspend fun updateTask(task: TaskModel)
    suspend fun deleteTask(task: TaskModel)
    suspend fun deleteCompletedTasks()
}