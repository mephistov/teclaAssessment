package com.example.teclaassessment.data.repository

import com.example.teclaassessment.data.local.TaskDao
import com.example.teclaassessment.data.local.toDomain
import com.example.teclaassessment.data.local.toEntity
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<TaskModel>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTasksByStatus(isCompleted: Boolean): Flow<List<TaskModel>> {
        return taskDao.getTasksByStatus(isCompleted).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(taskId: Int): TaskModel? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun insertTask(task: TaskModel) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: TaskModel) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: TaskModel) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}