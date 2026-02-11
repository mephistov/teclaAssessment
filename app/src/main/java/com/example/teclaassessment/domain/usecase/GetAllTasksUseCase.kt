package com.example.teclaassessment.domain.usecase

import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<TaskModel>> {
        return repository.getAllTasks()
    }
}