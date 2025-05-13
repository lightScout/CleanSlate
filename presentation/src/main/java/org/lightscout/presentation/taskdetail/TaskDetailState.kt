package org.lightscout.presentation.taskdetail

import org.lightscout.domain.model.Task

data class TaskDetailState(
        val task: Task? = null,
        val isLoading: Boolean = false,
        val isEditing: Boolean = false,
        val error: String? = null
)

sealed class TaskDetailEvent {
    data class LoadTask(val taskId: String) : TaskDetailEvent()
    data object EditTask : TaskDetailEvent()
    data object CancelEdit : TaskDetailEvent()
    data class UpdateTask(val title: String, val description: String) : TaskDetailEvent()
    data class ToggleTaskCompletion(val taskId: String) : TaskDetailEvent()
    data class DeleteTask(val taskId: String) : TaskDetailEvent()
}

sealed class TaskDetailEffect {
    data class ShowError(val message: String) : TaskDetailEffect()
    data object NavigateBack : TaskDetailEffect()
    data object TaskUpdated : TaskDetailEffect()
    data object TaskDeleted : TaskDetailEffect()
}
