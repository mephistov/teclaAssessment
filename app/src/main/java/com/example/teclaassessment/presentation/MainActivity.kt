package com.example.teclaassessment.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teclaassessment.presentation.compose.TaskListScreen
import com.example.teclaassessment.presentation.viewmodels.TaskViewModel
import com.example.teclaassessment.ui.theme.TeclaAssessmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeclaAssessmentTheme {
                TaskListRoute()
            }
        }
    }
}

@Composable
fun TaskListRoute(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.allTasks.collectAsState()

    TaskListScreen(
        tasks = tasks,
        onTaskToggle = viewModel::toggleTaskCompletion,
        onTaskDelete = viewModel::deleteTask,
        onTaskAdd = viewModel::insertTask
    )
}