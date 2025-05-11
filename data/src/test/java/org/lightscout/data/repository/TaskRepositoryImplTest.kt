package org.lightscout.data.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.lightscout.data.dto.TaskDto
import org.lightscout.data.mapper.toDto
import org.lightscout.data.remote.TaskApiService
import org.lightscout.domain.model.Task

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryImplTest {

    private lateinit var repository: TaskRepositoryImpl
    private lateinit var apiService: TaskApiService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        repository = TaskRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given a list of tasks from api, when getTasks is called, then returns mapped domain tasks`() =
            runTest {
                // Given
                val taskDtos =
                        listOf(
                                TaskDto(
                                        "1",
                                        "Task 1",
                                        "Description 1",
                                        false,
                                        1234567890,
                                        1234567890
                                ),
                                TaskDto(
                                        "2",
                                        "Task 2",
                                        "Description 2",
                                        true,
                                        1234567890,
                                        1234567890
                                )
                        )
                coEvery { apiService.getTasks() } returns taskDtos

                // When/Then
                repository.getTasks().test {
                    val tasks = awaitItem()
                    assertEquals(2, tasks.size)
                    assertEquals("Task 1", tasks[0].title)
                    assertEquals("Description 1", tasks[0].description)
                    assertEquals(false, tasks[0].isCompleted)
                    assertEquals("Task 2", tasks[1].title)
                    assertEquals("Description 2", tasks[1].description)
                    assertEquals(true, tasks[1].isCompleted)
                    awaitComplete()
                }
            }

    @Test
    fun `given api error, when getTasks is called, then throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { apiService.getTasks() } throws exception

        // When/Then
        assertFailsWith<RuntimeException> { repository.getTasks().first() }
    }

    @Test
    fun `given empty task list from api, when getTasks is called, then returns empty list`() =
            runTest {
                // Given
                coEvery { apiService.getTasks() } returns emptyList()

                // When/Then
                repository.getTasks().test {
                    val tasks = awaitItem()
                    assertEquals(0, tasks.size)
                    awaitComplete()
                }
            }

    @Test
    fun `given a task id, when getTaskById is called, then returns mapped domain task`() = runTest {
        // Given
        val taskDto = TaskDto("1", "Task 1", "Description 1", false, 1234567890, 1234567890)
        coEvery { apiService.getTaskById("1") } returns taskDto

        // When/Then
        repository.getTaskById("1").test {
            val task = awaitItem()
            assertEquals("1", task.id)
            assertEquals("Task 1", task.title)
            assertEquals("Description 1", task.description)
            assertEquals(false, task.isCompleted)
            assertEquals(1234567890, task.createdAt)
            assertEquals(1234567890, task.updatedAt)
            awaitComplete()
        }
    }

    @Test
    fun `given invalid task id, when getTaskById is called, then throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Task not found")
        coEvery { apiService.getTaskById("invalid") } throws exception

        // When/Then
        assertFailsWith<RuntimeException> { repository.getTaskById("invalid").first() }
    }

    @Test
    fun `given a new task, when createTask is called, then calls api service with mapped dto`() =
            runTest {
                // Given
                val task = Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890)
                val taskDto = task.toDto()
                coEvery { apiService.createTask(taskDto) } returns taskDto

                // When
                repository.createTask(task)

                // Then
                coVerify { apiService.createTask(taskDto) }
            }

    @Test
    fun `given api error, when createTask is called, then throws exception`() = runTest {
        // Given
        val task = Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890)
        val exception = RuntimeException("Network error")
        coEvery { apiService.createTask(any()) } throws exception

        // When/Then
        assertFailsWith<RuntimeException> { repository.createTask(task) }
    }

    @Test
    fun `given an existing task, when updateTask is called, then calls api service with mapped dto`() =
            runTest {
                // Given
                val task = Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890)
                val taskDto = task.toDto()
                coEvery { apiService.updateTask(task.id, taskDto) } returns taskDto

                // When
                repository.updateTask(task)

                // Then
                coVerify { apiService.updateTask(task.id, taskDto) }
            }

    @Test
    fun `given api error, when updateTask is called, then throws exception`() = runTest {
        // Given
        val task = Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890)
        val exception = RuntimeException("Network error")
        coEvery { apiService.updateTask(any(), any()) } throws exception

        // When/Then
        assertFailsWith<RuntimeException> { repository.updateTask(task) }
    }

    @Test
    fun `given a task id, when deleteTask is called, then calls api service with id`() = runTest {
        // Given
        val taskId = "1"
        coEvery { apiService.deleteTask(taskId) } returns Unit

        // When
        repository.deleteTask(taskId)

        // Then
        coVerify { apiService.deleteTask(taskId) }
    }

    @Test
    fun `given api error, when deleteTask is called, then throws exception`() = runTest {
        // Given
        val taskId = "1"
        val exception = RuntimeException("Network error")
        coEvery { apiService.deleteTask(taskId) } throws exception

        // When/Then
        assertFailsWith<RuntimeException> { repository.deleteTask(taskId) }
    }
}
