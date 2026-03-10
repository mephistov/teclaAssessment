package com.example.teclaassessment.domain.models

sealed class ValidationError {
    data class TitleTooLong(val maxLength: Int) : ValidationError()
    data class DescriptionTooLong(val maxLength: Int) : ValidationError()
    object TitleEmpty : ValidationError()
}
