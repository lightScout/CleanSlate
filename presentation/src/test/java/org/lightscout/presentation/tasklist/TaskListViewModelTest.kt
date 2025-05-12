package org.lightscout.presentation.tasklist

import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.lightscout.domain.model.Task
import org.lightscout.domain.usecase.CreateTaskUseCase
import org.lightscout.domain.usecase.DeleteTaskUseCase
import org.lightscout.domain.usecase.GetTaskByIdUseCase
import org.lightscout.domain.usecase.GetTasksUseCase
import org.lightscout.domain.usecase.UpdateTaskUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private lateinit var viewModel: TaskListViewModel
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTasksUseCase = mockk()
        getTaskByIdUseCase = mockk()
        createTaskUseCase = mockk()
        updateTaskUseCase = mockk()
        deleteTaskUseCase = mockk()

        coEvery { getTasksUseCase() } returns flow { emit(emptyList()) }

        viewModel =
                TaskListViewModel(
                        getTasksUseCase = getTasksUseCase,
                        getTaskByIdUseCase = getTaskByIdUseCase,
                        createTaskUseCase = createTaskUseCase,
                        updateTaskUseCase = updateTaskUseCase,
                        deleteTaskUseCase = deleteTaskUseCase
                )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when initialized, loads tasks successfully`() = runTest {
        // Given
        val mockTasks =
                listOf(
                        Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890),
                        Task("2", "Task 2", "Description 2", true, 1234567890, 1234567890)
                )

        // Set up the mock behavior for this test
        coEvery { getTasksUseCase() } returns flow { emit(mockTasks) }

        // When - Trigger a reload
        viewModel.onEvent(TaskListEvent.LoadTasks)
        advanceUntilIdle()

        // Then - Verify tasks are loaded
        assertEquals(mockTasks, viewModel.state.value.tasks)
        assertNull(viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `when loading tasks fails, shows error`() = runTest {
        // Given
        val errorMessage = "Failed to load tasks"

        // Set up the mock behavior for this test
        coEvery { getTasksUseCase() } returns flow { throw RuntimeException(errorMessage) }

        // Collect effects first to ensure emission is catch
        val effectJob = launch {
            viewModel.effect.collect {
                if (it is TaskListEffect.ShowError) {
                    assertEquals(errorMessage, it.message)
                    cancel()
                }
            }
        }

        // When - Trigger a reload
        viewModel.onEvent(TaskListEvent.LoadTasks)
        advanceUntilIdle()

        // Then - Verify error state
        assertEquals(emptyList(), viewModel.state.value.tasks)
        assertEquals(errorMessage, viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)

        // Cleanup
        effectJob.cancel()
    }

    @Test
    fun `when creating task, adds to list and emits effect`() = runTest {
        // Given
        val title = "New Task"
        val description = "New Description"
        val newTask = Task("3", title, description, false, 1234567890, 1234567890)

        // Set up the mock behavior for this test
        coEvery { getTasksUseCase() } returns flow { emit(emptyList()) }
        coEvery { createTaskUseCase(title, description) } returns newTask

        // Collect effects first to ensure emission is catch
        val effectJob = launch {
            viewModel.effect.collect {
                if (it is TaskListEffect.TaskCreated) {
                    cancel()
                }
            }
        }

        // Trigger a reload to ensure we have initial empty list
        viewModel.onEvent(TaskListEvent.LoadTasks)
        advanceUntilIdle()

        // When - Create a task
        viewModel.onEvent(TaskListEvent.CreateTask(title, description))
        advanceUntilIdle()

        // Then - Verify task is added
        assertEquals(listOf(newTask), viewModel.state.value.tasks)

        // Cleanup
        effectJob.cancel()
    }

    @Test
    fun `when deleting task, removes from list and emits effect`() = runTest {
        // Given
        val taskId = "1"
        val mockTasks =
                listOf(
                        Task(taskId, "Task 1", "Description 1", false, 1234567890, 1234567890),
                        Task("2", "Task 2", "Description 2", true, 1234567890, 1234567890)
                )

        // Set up the mock behavior for this test
        coEvery { getTasksUseCase() } returns flow { emit(mockTasks) }
        coEvery { deleteTaskUseCase(taskId) } returns Unit

        // Collect effects first to ensure emission is catch
        val effectJob = launch {
            viewModel.effect.collect {
                if (it is TaskListEffect.TaskDeleted) {
                    cancel()
                }
            }
        }

        // Trigger a reload to get the mock tasks
        viewModel.onEvent(TaskListEvent.LoadTasks)
        advanceUntilIdle()

        // When - Delete a task
        viewModel.onEvent(TaskListEvent.DeleteTask(taskId))
        advanceUntilIdle()

        // Then - Check that the task list no longer contains the deleted task
        assertEquals(mockTasks.filter { it.id != taskId }, viewModel.state.value.tasks)

        // Cleanup
        effectJob.cancel()
    }

    @Test
    fun `when toggling task completion, updates task and emits effect`() = runTest {
        // Given
        val taskId = "1"
        val mockTasks =
                listOf(
                        Task(taskId, "Task 1", "Description 1", false, 1234567890, 1234567890),
                        Task("2", "Task 2", "Description 2", true, 1234567890, 1234567890)
                )
        val updatedTask = mockTasks[0].copy(isCompleted = true)

        // Set up the mock behavior for this test
        coEvery { getTasksUseCase() } returns flow { emit(mockTasks) }
        coEvery { updateTaskUseCase(any()) } returns updatedTask

        // Collect effects first to ensure emission is catch
        val effectJob = launch {
            viewModel.effect.collect {
                if (it is TaskListEffect.TaskUpdated) {
                    cancel()
                }
            }
        }

        // Trigger a reload to get the mock tasks
        viewModel.onEvent(TaskListEvent.LoadTasks)
        advanceUntilIdle()

        // When - Toggle task completion
        viewModel.onEvent(TaskListEvent.ToggleTaskCompletion(taskId))
        advanceUntilIdle()

        // Then - Verify task is updated
        val updatedTaskInState = viewModel.state.value.tasks.find { it.id == taskId }
        assertEquals(true, updatedTaskInState?.isCompleted)

        // Cleanup
        effectJob.cancel()
    }

    @Test
    fun `when operation fails, shows error`() = runTest {
        // Given
        val errorMessage = "Operation failed"
        val taskId = "1"

        // Set up the mock behavior for this test
        coEvery { deleteTaskUseCase(any()) } throws RuntimeException(errorMessage)

        // Collect effects first to ensure emission is catch
        val effectJob = launch {
            viewModel.effect.collect {
                if (it is TaskListEffect.ShowError && it.message == errorMessage) {
                    cancel()
                }
            }
        }

        // When - Delete with error
        viewModel.onEvent(TaskListEvent.DeleteTask(taskId))
        advanceUntilIdle()

        // Cleanup
        effectJob.cancel()
    }
}
