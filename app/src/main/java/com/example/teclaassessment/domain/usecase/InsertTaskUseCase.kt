package com.example.teclaassessment.domain.usecase

import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.repository.TaskRepository
import com.example.teclaassessment.domain.models.Result
import javax.inject.Inject

class InsertTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    // Validate input and call repository
    suspend operator fun invoke(title: String, description: String): Result<Unit> {
        // Validate title length (max 50 characters)
        if (title.length > TITLE_MAX_LENGTH) {
            return Result.Error("Title cannot exceed $TITLE_MAX_LENGTH characters")
        }

        // Validate description length (max 200 characters)
        if (description.length > DESCRIPTION_MAX_LENGTH) {
            return Result.Error("Description cannot exceed $DESCRIPTION_MAX_LENGTH characters")
        }

        // Validate title is not blank
        if (title.isBlank()) {
            return Result.Error("Title cannot be empty")
        }

        val task = TaskModel(
            title = title.trim(),
            description = description.trim()
        )

        return repository.insertTask(task)
    }

    companion object {
        const val TITLE_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 200
    }
}