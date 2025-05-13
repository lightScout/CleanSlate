package org.lightscout.presentation.tasklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.lightscout.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToTaskDetail: (String) -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TaskListEffect.ShowError -> {
                    // Show error snackbar
                }

                is TaskListEffect.NavigateToTaskDetail -> {
                    onNavigateToTaskDetail(effect.taskId)
                }

                is TaskListEffect.TaskCreated -> {
                    showCreateDialog = false
                }

                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag(TestTags.ADD_TASK_BUTTON)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Task",
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier =
                    Modifier
                        .align(Alignment.Center)
                        .semantics { contentDescription = "Loading indicator" }
                        .testTag(TestTags.LOADING_INDICATOR)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(TestTags.TASK_LIST),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { viewModel.onEvent(TaskListEvent.UpdateTask(task)) },
                            onToggleCompletion = {
                                viewModel.onEvent(TaskListEvent.ToggleTaskCompletion(task.id))
                            },
                            onDelete = { viewModel.onEvent(TaskListEvent.DeleteTask(task.id)) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTaskDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, description ->
                viewModel.onEvent(TaskListEvent.CreateTask(title, description))
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onTaskClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${TestTags.TASK_ITEM_PREFIX}${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion() },
                    modifier = Modifier.semantics { contentDescription = TestTags.CHECKBOX }
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("${TestTags.TASK_TITLE_PREFIX}${task.id}")
                    )
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                        Modifier.testTag(
                            "${TestTags.TASK_DESCRIPTION_PREFIX}${task.id}"
                        )
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    modifier = Modifier.testTag("${TestTags.DELETE_TASK_PREFIX}${task.id}")
                )
            }
        }
    }
}

@Composable
fun CreateTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.TASK_TITLE_INPUT)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.TASK_DESCRIPTION_INPUT)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description)
                    }
                },
                modifier = Modifier.testTag(TestTags.CREATE_TASK_BUTTON)
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag(TestTags.CANCEL_TASK_BUTTON)
            ) { Text("Cancel") }
        }
    )
}

object TestTags {
    const val ADD_TASK_BUTTON = "add_task_button"
    const val LOADING_INDICATOR = "loading_indicator"
    const val TASK_LIST = "task_list"
    const val TASK_ITEM_PREFIX = "task_item_"
    const val CHECKBOX = "Checkbox"
    const val TASK_TITLE_PREFIX = "task_title_"
    const val TASK_DESCRIPTION_PREFIX = "task_description_"
    const val DELETE_TASK_PREFIX = "delete_task_"
    const val TASK_TITLE_INPUT = "task_title_input"
    const val TASK_DESCRIPTION_INPUT = "task_description_input"
    const val CREATE_TASK_BUTTON = "create_task_button"
    const val CANCEL_TASK_BUTTON = "cancel_task_button"
}
