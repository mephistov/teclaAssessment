package com.example.teclaassessment.domain.usecase

import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.domain.repository.TaskRepository
import com.example.teclaassessment.domain.models.Result
import com.example.teclaassessment.domain.models.ValidationError
import javax.inject.Inject

class InsertTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    // Validate input and call repository
    suspend operator fun invoke(title: String, description: String): Result<Unit> {
        // Validate title length (max 50 characters)
        if (title.length > TITLE_MAX_LENGTH) {
            return Result.Error(validationError = ValidationError.TitleTooLong(TITLE_MAX_LENGTH))
        }

        // Validate description length (max 200 characters)
        if (description.length > DESCRIPTION_MAX_LENGTH) {
            return Result.Error(validationError = ValidationError.DescriptionTooLong(DESCRIPTION_MAX_LENGTH))
        }

        // Validate title is not blank
        if (title.isBlank()) {
            return Result.Error(validationError = ValidationError.TitleEmpty)
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