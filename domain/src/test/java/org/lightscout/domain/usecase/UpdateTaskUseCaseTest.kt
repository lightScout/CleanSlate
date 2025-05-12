package org.lightscout.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class UpdateTaskUseCaseTest {

    private lateinit var useCase: UpdateTaskUseCase
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = UpdateTaskUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given valid task data, when invoke is called, then updates task`() = runTest {
        // Given
        val task = Task("1", "Old Title", "Old Description", false, 1234567890, 1234567890)
        val updatedTask = Task("1", "New Title", "New Description", true, 1234567890, 1234567891)
        coEvery { repository.updateTask(any()) } returns updatedTask

        // When
        val result =
                useCase(
                        task.copy(
                                title = "New Title",
                                description = "New Description",
                                isCompleted = true
                        )
                )

        // Then
        assertEquals("1", result.id)
        assertEquals("New Title", result.title)
        assertEquals("New Description", result.description)
        assertEquals(true, result.isCompleted)
        assertEquals(1234567890, result.createdAt)
        assertEquals(1234567891, result.updatedAt)
        coVerify { repository.updateTask(any()) }
    }

    @Test
    fun `given empty title, when invoke is called, then throws exception`() = runTest {
        // Given
        val task = Task("1", "Title", "Description", false, 1234567890, 1234567890)

        // When/Then
        assertFailsWith<IllegalArgumentException> { useCase(task.copy(title = "")) }
    }

    @Test
    fun `given repository throws exception, when invoke is called, then propagates exception`() =
            runTest {
                // Given
                val task = Task("1", "Title", "Description", false, 1234567890, 1234567890)
                val exception = RuntimeException("Failed to update task")
                coEvery { repository.updateTask(any()) } throws exception

                // When/Then
                assertFailsWith<RuntimeException> { useCase(task) }
            }
}
