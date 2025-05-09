package org.lightscout.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.lightscout.data.mapper.toDomain
import org.lightscout.data.mapper.toDto
import org.lightscout.data.remote.TaskApiService
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

class TaskRepositoryImpl @Inject constructor(private val apiService: TaskApiService) :
        TaskRepository {

    override fun getTasks(): Flow<List<Task>> = flow {
        val tasks = apiService.getTasks().map { it.toDomain() }
        emit(tasks)
    }

    override fun getTaskById(id: String): Flow<Task> = flow {
        val task = apiService.getTaskById(id).toDomain()
        emit(task)
    }

    override suspend fun createTask(task: Task) {
        apiService.createTask(task.toDto())
    }

    override suspend fun updateTask(task: Task) {
        apiService.updateTask(task.id, task.toDto())
    }

    override suspend fun deleteTask(id: String) {
        apiService.deleteTask(id)
    }
}
