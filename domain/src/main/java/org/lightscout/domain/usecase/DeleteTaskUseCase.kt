package org.lightscout.domain.usecase

import javax.inject.Inject
import org.lightscout.domain.repository.TaskRepository

class DeleteTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: String) {
        if (taskId.isBlank()) {
            throw IllegalArgumentException("Task ID cannot be empty")
        }
        repository.deleteTask(taskId)
    }
}
