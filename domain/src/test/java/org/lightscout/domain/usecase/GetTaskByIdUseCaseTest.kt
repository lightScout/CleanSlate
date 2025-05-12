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
class GetTaskByIdUseCaseTest {

    private lateinit var useCase: GetTaskByIdUseCase
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetTaskByIdUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given valid task id, when invoke is called, then returns task`() = runTest {
        // Given
        val task = Task("1", "Test Task", "Test Description", false, 1234567890, 1234567890)
        coEvery { repository.getTaskById("1") } returns flow { emit(task) }

        // When/Then
        useCase("1").test {
            val result = awaitItem()
            assertEquals("1", result.id)
            assertEquals("Test Task", result.title)
            assertEquals("Test Description", result.description)
            assertEquals(false, result.isCompleted)
            assertEquals(1234567890, result.createdAt)
            assertEquals(1234567890, result.updatedAt)
            awaitComplete()
        }
    }

    @Test
    fun `given repository throws exception, when invoke is called, then propagates exception`() =
            runTest {
                // Given
                val exception = RuntimeException("Task not found")
                coEvery { repository.getTaskById("invalid") } returns flow { throw exception }

                // When/Then
                useCase("invalid").test {
                    val error = awaitError()
                    assertEquals(exception, error)
                }
            }
}
