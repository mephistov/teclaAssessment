package com.example.teclaassessment.presentation.compose

import com.example.teclaassessment.domain.models.TaskModel

sealed class TaskListEvent {
    data class OnTaskToggle(val task: TaskModel) : TaskListEvent()
    data class OnTaskDelete(val task: TaskModel) : TaskListEvent()
    data class OnTaskAdd(val title: String, val description: String) : TaskListEvent()
    object OnErrorDismiss : TaskListEvent()
}
