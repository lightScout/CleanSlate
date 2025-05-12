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
class CreateTaskUseCaseTest {

    private lateinit var useCase: CreateTaskUseCase
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = CreateTaskUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given valid task data, when invoke is called, then creates task`() = runTest {
        // Given
        val title = "New Task"
        val description = "New Description"
        val createdTask = Task("1", title, description, false, 1234567890, 1234567890)
        coEvery { repository.createTask(any()) } returns createdTask

        // When
        val result = useCase(title, description)

        // Then
        assertEquals("1", result.id)
        assertEquals(title, result.title)
        assertEquals(description, result.description)
        assertEquals(false, result.isCompleted)
        assertEquals(1234567890, result.createdAt)
        assertEquals(1234567890, result.updatedAt)
        coVerify { repository.createTask(any()) }
    }

    @Test
    fun `given empty title, when invoke is called, then throws exception`() = runTest {
        // Given
        val title = ""
        val description = "Description"

        // When/Then
        assertFailsWith<IllegalArgumentException> { useCase(title, description) }
    }

    @Test
    fun `given repository throws exception, when invoke is called, then propagates exception`() =
            runTest {
                // Given
                val title = "New Task"
                val description = "New Description"
                val exception = RuntimeException("Failed to create task")
                coEvery { repository.createTask(any()) } throws exception

                // When/Then
                assertFailsWith<RuntimeException> { useCase(title, description) }
            }
}
