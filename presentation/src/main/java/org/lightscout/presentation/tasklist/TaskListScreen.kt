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
                            IconButton(onClick = { showCreateDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Task")
                            }
                        }
                )
            }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
    Card(onClick = onTaskClick, modifier = Modifier.fillMaxWidth()) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleCompletion() })
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
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
                            modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description)
                            }
                        }
                ) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
