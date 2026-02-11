package com.example.teclaassessment.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teclaassessment.R
import com.example.teclaassessment.domain.models.TaskModel
import com.example.teclaassessment.ui.theme.TeclaAssessmentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskModel>,
    onTaskToggle: (TaskModel) -> Unit,
    onTaskDelete: (TaskModel) -> Unit,
    onTaskAdd: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_task)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { padding ->
        if (tasks.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            TaskList(
                tasks = tasks,
                onTaskToggle = onTaskToggle,
                onTaskDelete = onTaskDelete,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, description ->
                onTaskAdd(title, description)
                showDialog = false
            }
        )
    }
}

@Composable
private fun TaskList(
    tasks: List<TaskModel>,
    onTaskToggle: (TaskModel) -> Unit,
    onTaskDelete: (TaskModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            TaskItem(
                task = task,
                onToggle = { onTaskToggle(task) },
                onDelete = { onTaskDelete(task) }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: TaskModel,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else null
                )

                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No tasks.\n¡Add a new one!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListScreenPreview() {
    TeclaAssessmentTheme {
        TaskListScreen(
            tasks = listOf(
                TaskModel(1, "Buy Milk", "In the supermarket", false),
                TaskModel(2, "Do some excersise", "30 minuts", true),
                TaskModel(3, "Study Kotlin", "Advance compose", false)
            ),
            onTaskToggle = {},
            onTaskDelete = {},
            onTaskAdd = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListScreenEmptyPreview() {
    TeclaAssessmentTheme {
        TaskListScreen(
            tasks = emptyList(),
            onTaskToggle = {},
            onTaskDelete = {},
            onTaskAdd = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskItemPreview() {
    TeclaAssessmentTheme {
        TaskItem(
            task = TaskModel(1, "example task", "Description", false),
            onToggle = {},
            onDelete = {}
        )
    }
}