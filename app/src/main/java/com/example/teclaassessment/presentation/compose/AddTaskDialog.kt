package com.example.teclaassessment.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teclaassessment.R
import com.example.teclaassessment.domain.usecase.InsertTaskUseCase
import com.example.teclaassessment.ui.theme.TeclaAssessmentTheme


@Composable
fun AddTaskDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    // Character limits
    val titleMaxLength = InsertTaskUseCase.TITLE_MAX_LENGTH
    val descriptionMaxLength = InsertTaskUseCase.DESCRIPTION_MAX_LENGTH

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.new_task),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title field with character counter
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        // Enforce max length
                        if (it.length <= titleMaxLength) {
                            title = it
                            if (titleError && it.isNotBlank()) {
                                titleError = false
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.title_label)) },
                    placeholder = { Text(stringResource(R.string.title_placeholder)) },
                    isError = titleError || title.length == titleMaxLength,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (titleError) {
                                Text(
                                    text = stringResource(R.string.title_required_error),
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            // Character counter
                            Text(
                                text = "${title.length}/$titleMaxLength",
                                color = if (title.length == titleMaxLength) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                // Description field with character counter
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        // Enforce max length
                        if (it.length <= descriptionMaxLength) {
                            description = it
                        }
                    },
                    label = { Text(stringResource(R.string.description_label)) },
                    placeholder = { Text(stringResource(R.string.description_placeholder)) },
                    supportingText = {
                        // Character counter
                        Text(
                            text = "${description.length}/$descriptionMaxLength",
                            modifier = Modifier.fillMaxWidth(),
                            color = if (description.length == descriptionMaxLength) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 4,
                    enabled = !isLoading
                )

                // Loading indicator
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Adding task...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onConfirm(title.trim(), description.trim())
                    }
                },
                enabled = !isLoading && title.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}

// Preview
@Preview(showBackground = true)
@Composable
private fun AddTaskDialogPreview() {
    TeclaAssessmentTheme {
        AddTaskDialog(
            isLoading = false,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTaskDialogPreviewLoading() {
    TeclaAssessmentTheme {
        AddTaskDialog(
            isLoading = true,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}

