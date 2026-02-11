package com.example.teclaassessment.domain.usecase

import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskModel) {
        repository.updateTask(task)
    }
}