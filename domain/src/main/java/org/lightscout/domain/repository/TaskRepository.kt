package org.lightscout.domain.repository

import kotlinx.coroutines.flow.Flow
import org.lightscout.domain.model.Task

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task>
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
}
