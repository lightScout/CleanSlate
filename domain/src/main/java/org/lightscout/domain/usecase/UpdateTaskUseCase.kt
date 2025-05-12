package org.lightscout.domain.usecase

import javax.inject.Inject
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

class UpdateTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Task {
        if (task.title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }

        val updatedTask = task.copy(updatedAt = System.currentTimeMillis())

        return repository.updateTask(updatedTask)
    }
}
