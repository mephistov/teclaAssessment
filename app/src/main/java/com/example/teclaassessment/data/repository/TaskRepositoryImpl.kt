package com.example.teclaassessment.data.repository

import com.example.teclaassessment.data.local.TaskDao
import com.example.teclaassessment.data.local.toDomain
import com.example.teclaassessment.data.local.toEntity
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.random.Random
import com.example.teclaassessment.domain.models.Result

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

    // Simulate network call with random delay and 75% success rate
    override suspend fun insertTask(task: TaskModel): Result<Unit> {
        return try {
            // Random delay between 0.5 and 2.5 seconds (500-2500 ms)
            val delayMillis = Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS)
            delay(delayMillis)

            // Simulate 75% success rate (25% failure)
            val isSuccess = Random.nextFloat() < SUCCESS_RATE

            if (isSuccess) {
                taskDao.insertTask(task.toEntity())
                Result.Success(Unit)
            } else {
                // Simulate network error
                Result.Error(
                    message = "Network error: Failed to add task. Please try again.",
                    exception = NetworkSimulationException("Simulated network failure")
                )
            }
        } catch (e: Exception) {
            Result.Error(
                message = "Unexpected error: ${e.localizedMessage}",
                exception = e
            )
        }
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

    companion object {
        private const val MIN_DELAY_MS = 500L  // 0.5 seconds
        private const val MAX_DELAY_MS = 2500L // 2.5 seconds
        private const val SUCCESS_RATE = 0.75f // 75% success rate
    }
}

// Custom exception for network simulation
class NetworkSimulationException(message: String) : Exception(message)