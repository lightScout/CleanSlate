package org.lightscout.presentation.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lightscout.domain.usecase.DeleteTaskUseCase
import org.lightscout.domain.usecase.GetTaskByIdUseCase
import org.lightscout.domain.usecase.UpdateTaskUseCase

@HiltViewModel
class TaskDetailViewModel
@Inject
constructor(
        private val getTaskByIdUseCase: GetTaskByIdUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TaskDetailEffect>()
    val effect: SharedFlow<TaskDetailEffect> = _effect.asSharedFlow()

    init {
        savedStateHandle.get<String>("taskId")?.let { taskId -> loadTask(taskId) }
    }

    fun onEvent(event: TaskDetailEvent) {
        when (event) {
            is TaskDetailEvent.LoadTask -> loadTask(event.taskId)
            is TaskDetailEvent.EditTask -> _state.update { it.copy(isEditing = true) }
            is TaskDetailEvent.CancelEdit -> _state.update { it.copy(isEditing = false) }
            is TaskDetailEvent.UpdateTask -> updateTaskDetails(event.title, event.description)
            is TaskDetailEvent.ToggleTaskCompletion -> toggleTaskCompletion(event.taskId)
            is TaskDetailEvent.DeleteTask -> deleteTask(event.taskId)
        }
    }

    private fun loadTask(taskId: String) {
        _state.update { it.copy(isLoading = true) }
        getTaskByIdUseCase(taskId)
                .onEach { task ->
                    _state.update { it.copy(task = task, isLoading = false, error = null) }
                }
                .catch { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.emit(TaskDetailEffect.ShowError(error.message ?: "Failed to load task"))
                }
                .launchIn(viewModelScope)
    }

    private fun updateTaskDetails(title: String, description: String) {
        viewModelScope.launch {
            try {
                _state.value.task?.let { currentTask ->
                    val updatedTask = currentTask.copy(title = title, description = description)
                    val result = updateTaskUseCase(updatedTask)
                    _state.update { it.copy(task = result, isEditing = false) }
                    _effect.emit(TaskDetailEffect.TaskUpdated)
                }
            } catch (e: Exception) {
                _effect.emit(TaskDetailEffect.ShowError(e.message ?: "Failed to update task"))
            }
        }
    }

    private fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            try {
                _state.value.task?.let { currentTask ->
                    val updatedTask = currentTask.copy(isCompleted = !currentTask.isCompleted)
                    val result = updateTaskUseCase(updatedTask)
                    _state.update { it.copy(task = result) }
                    _effect.emit(TaskDetailEffect.TaskUpdated)
                }
            } catch (e: Exception) {
                _effect.emit(TaskDetailEffect.ShowError(e.message ?: "Failed to update task"))
            }
        }
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
                _effect.emit(TaskDetailEffect.TaskDeleted)
                _effect.emit(TaskDetailEffect.NavigateBack)
            } catch (e: Exception) {
                _effect.emit(TaskDetailEffect.ShowError(e.message ?: "Failed to delete task"))
            }
        }
    }
}
