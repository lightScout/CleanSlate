package org.lightscout.data.repository

import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.lightscout.data.mock.MockTaskProvider
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

class MockTaskRepository @Inject constructor() : TaskRepository {
    private val tasks = mutableListOf<Task>().apply { addAll(MockTaskProvider.getMockTasks()) }

    override fun getTasks(): Flow<List<Task>> = flow { emit(tasks) }

    override fun getTaskById(id: String): Flow<Task> = flow {
        val task =
                tasks.find { it.id == id }
                        ?: throw IllegalArgumentException("Task not found with id: $id")
        emit(task)
    }

    override suspend fun createTask(task: Task): Task {
        val newTask =
                task.copy(
                        id = task.id.ifEmpty { java.util.UUID.randomUUID().toString() },
                        createdAt = Instant.now().epochSecond,
                        updatedAt = Instant.now().epochSecond
                )
        tasks.add(newTask)
        return newTask
    }

    override suspend fun updateTask(task: Task): Task {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index == -1) {
            throw IllegalArgumentException("Task not found with id: ${task.id}")
        }
        val updatedTask = task.copy(updatedAt = Instant.now().epochSecond)
        tasks[index] = updatedTask
        return updatedTask
    }

    override suspend fun deleteTask(id: String) {
        val task =
                tasks.find { it.id == id }
                        ?: throw IllegalArgumentException("Task not found with id: $id")
        tasks.remove(task)
    }
}
