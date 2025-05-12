package org.lightscout.presentation.tasklist

import org.lightscout.domain.model.Task

data class TaskListState(
        val tasks: List<Task> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
)

sealed class TaskListEvent {
    data object LoadTasks : TaskListEvent()
    data class ToggleTaskCompletion(val taskId: String) : TaskListEvent()
    data class DeleteTask(val taskId: String) : TaskListEvent()
    data class CreateTask(val title: String, val description: String) : TaskListEvent()
    data class UpdateTask(val task: Task) : TaskListEvent()
}

sealed class TaskListEffect {
    data class ShowError(val message: String) : TaskListEffect()
    data class NavigateToTaskDetail(val taskId: String) : TaskListEffect()
    data object TaskCreated : TaskListEffect()
    data object TaskUpdated : TaskListEffect()
    data object TaskDeleted : TaskListEffect()
}
