package org.lightscout.presentation.tasklist

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
import org.lightscout.domain.model.Task
import org.lightscout.domain.usecase.CreateTaskUseCase
import org.lightscout.domain.usecase.DeleteTaskUseCase
import org.lightscout.domain.usecase.GetTaskByIdUseCase
import org.lightscout.domain.usecase.GetTasksUseCase
import org.lightscout.domain.usecase.UpdateTaskUseCase

@HiltViewModel
class TaskListViewModel
@Inject
constructor(
        private val getTasksUseCase: GetTasksUseCase,
        private val getTaskByIdUseCase: GetTaskByIdUseCase,
        private val createTaskUseCase: CreateTaskUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TaskListEffect>()
    val effect: SharedFlow<TaskListEffect> = _effect.asSharedFlow()

    init {
        loadTasks()
    }

    fun onEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.LoadTasks -> loadTasks()
            is TaskListEvent.ToggleTaskCompletion -> toggleTaskCompletion(event.taskId)
            is TaskListEvent.DeleteTask -> deleteTask(event.taskId)
            is TaskListEvent.CreateTask -> createTask(event.title, event.description)
            is TaskListEvent.UpdateTask -> updateTask(event.task)
        }
    }

    private fun loadTasks() {
        getTasksUseCase()
                .onEach { tasks ->
                    _state.update { it.copy(tasks = tasks, isLoading = false, error = null) }
                }
                .catch { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.emit(TaskListEffect.ShowError(error.message ?: "Failed to load tasks"))
                }
                .launchIn(viewModelScope)
    }

    private fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            try {
                val task = state.value.tasks.find { it.id == taskId }
                task?.let {
                    val updatedTask = it.copy(isCompleted = !it.isCompleted)
                    updateTaskUseCase(updatedTask)
                    _effect.emit(TaskListEffect.TaskUpdated)
                }
            } catch (e: Exception) {
                _effect.emit(TaskListEffect.ShowError(e.message ?: "Failed to update task"))
            }
        }
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
                _effect.emit(TaskListEffect.TaskDeleted)
            } catch (e: Exception) {
                _effect.emit(TaskListEffect.ShowError(e.message ?: "Failed to delete task"))
            }
        }
    }

    private fun createTask(title: String, description: String) {
        viewModelScope.launch {
            try {
                createTaskUseCase(title, description)
                _effect.emit(TaskListEffect.TaskCreated)
            } catch (e: Exception) {
                _effect.emit(TaskListEffect.ShowError(e.message ?: "Failed to create task"))
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                updateTaskUseCase(task)
                _effect.emit(TaskListEffect.TaskUpdated)
            } catch (e: Exception) {
                _effect.emit(TaskListEffect.ShowError(e.message ?: "Failed to update task"))
            }
        }
    }
}
