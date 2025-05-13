package org.lightscout.presentation.taskdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.lightscout.domain.model.Task

object TaskDetailTestTags {
    const val SCREEN = "task_detail_screen"
    const val LOADING_INDICATOR = "task_detail_loading_indicator"
    const val TITLE_FIELD = "task_detail_title_field"
    const val DESCRIPTION_FIELD = "task_detail_description_field"
    const val COMPLETION_CHECKBOX = "task_detail_completion_checkbox"
    const val EDIT_BUTTON = "task_detail_edit_button"
    const val DELETE_BUTTON = "task_detail_delete_button"
    const val SAVE_BUTTON = "task_detail_save_button"
    const val CANCEL_BUTTON = "task_detail_cancel_button"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
        taskId: String,
        onNavigateBack: () -> Unit,
        viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(TaskDetailEvent.LoadTask(taskId))

        viewModel.effect.collect { effect ->
            when (effect) {
                is TaskDetailEffect.NavigateBack -> onNavigateBack()
                is TaskDetailEffect.ShowError -> {
                    // Show error message
                }
                else -> {}
            }
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(text = if (state.isEditing) "Edit Task" else "Task Details")
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            if (!state.isEditing) {
                                IconButton(
                                        onClick = { viewModel.onEvent(TaskDetailEvent.EditTask) },
                                        modifier = Modifier.testTag(TaskDetailTestTags.EDIT_BUTTON)
                                ) {
                                    Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Task"
                                    )
                                }

                                IconButton(
                                        onClick = {
                                            state.task?.let { task ->
                                                viewModel.onEvent(
                                                        TaskDetailEvent.DeleteTask(task.id)
                                                )
                                            }
                                        },
                                        modifier =
                                                Modifier.testTag(TaskDetailTestTags.DELETE_BUTTON)
                                ) {
                                    Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Task"
                                    )
                                }
                            }
                        }
                )
            }
    ) { paddingValues ->
        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .testTag(TaskDetailTestTags.SCREEN)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                        modifier =
                                Modifier.align(Alignment.Center)
                                        .testTag(TaskDetailTestTags.LOADING_INDICATOR)
                )
            } else {
                state.task?.let { task ->
                    if (state.isEditing) {
                        TaskEditForm(
                                task = task,
                                onSave = { title, description ->
                                    viewModel.onEvent(
                                            TaskDetailEvent.UpdateTask(title, description)
                                    )
                                },
                                onCancel = { viewModel.onEvent(TaskDetailEvent.CancelEdit) }
                        )
                    } else {
                        TaskDetails(
                                task = task,
                                onToggleCompletion = {
                                    viewModel.onEvent(TaskDetailEvent.ToggleTaskCompletion(task.id))
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetails(task: Task, onToggleCompletion: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = task.description, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Completion status
            Button(
                    onClick = onToggleCompletion,
                    modifier = Modifier.testTag(TaskDetailTestTags.COMPLETION_CHECKBOX)
            ) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleCompletion() })
                Text(
                        text = if (task.isCompleted) "Completed" else "Mark as completed",
                        modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TaskEditForm(task: Task, onSave: (String, String) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth().testTag(TaskDetailTestTags.TITLE_FIELD)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier =
                        Modifier.fillMaxWidth()
                                .height(200.dp)
                                .testTag(TaskDetailTestTags.DESCRIPTION_FIELD)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                    onClick = onCancel,
                    modifier =
                            Modifier.align(Alignment.CenterStart)
                                    .testTag(TaskDetailTestTags.CANCEL_BUTTON)
            ) { Text("Cancel") }

            Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(title, description)
                        }
                    },
                    modifier =
                            Modifier.align(Alignment.CenterEnd)
                                    .testTag(TaskDetailTestTags.SAVE_BUTTON)
            ) { Text("Save") }
        }
    }
}
