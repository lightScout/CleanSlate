package org.lightscout.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
import org.lightscout.domain.repository.TaskRepository

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteTaskUseCaseTest {

    private lateinit var useCase: DeleteTaskUseCase
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = DeleteTaskUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given valid task id, when invoke is called, then deletes task`() = runTest {
        // Given
        val taskId = "1"
        coEvery { repository.deleteTask(taskId) } returns Unit

        // When
        useCase(taskId)

        // Then
        coVerify { repository.deleteTask(taskId) }
    }

    @Test
    fun `given empty task id, when invoke is called, then throws exception`() = runTest {
        // Given
        val taskId = ""

        // When/Then
        assertFailsWith<IllegalArgumentException> { useCase(taskId) }
    }

    @Test
    fun `given repository throws exception, when invoke is called, then propagates exception`() =
            runTest {
                // Given
                val taskId = "1"
                val exception = RuntimeException("Failed to delete task")
                coEvery { repository.deleteTask(taskId) } throws exception

                // When/Then
                assertFailsWith<RuntimeException> { useCase(taskId) }
            }
}
