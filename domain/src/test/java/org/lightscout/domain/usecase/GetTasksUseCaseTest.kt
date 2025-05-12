package org.lightscout.domain.usecase

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

@OptIn(ExperimentalCoroutinesApi::class)
class GetTasksUseCaseTest {

    private lateinit var useCase: GetTasksUseCase
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetTasksUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given repository returns tasks, when invoke is called, then returns tasks`() = runTest {
        // Given
        val tasks =
                listOf(
                        Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890),
                        Task("2", "Task 2", "Description 2", true, 1234567890, 1234567890)
                )
        coEvery { repository.getTasks() } returns flow { emit(tasks) }

        // When/Then
        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("1", result[0].id)
            assertEquals("Task 1", result[0].title)
            assertEquals("Description 1", result[0].description)
            assertEquals(false, result[0].isCompleted)
            assertEquals("2", result[1].id)
            assertEquals("Task 2", result[1].title)
            assertEquals("Description 2", result[1].description)
            assertEquals(true, result[1].isCompleted)
            awaitComplete()
        }
    }

    @Test
    fun `given repository returns empty list, when invoke is called, then returns empty list`() =
            runTest {
                // Given
                coEvery { repository.getTasks() } returns flow { emit(emptyList()) }

                // When/Then
                useCase().test {
                    val result = awaitItem()
                    assertEquals(0, result.size)
                    awaitComplete()
                }
            }

    @Test
    fun `given repository throws exception, when invoke is called, then propagates exception`() =
            runTest {
                // Given
                val exception = RuntimeException("Network error")
                coEvery { repository.getTasks() } returns flow { throw exception }

                // When/Then
                useCase().test {
                    val error = awaitError()
                    assertEquals(exception, error)
                }
            }
}
