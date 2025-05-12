package org.lightscout.domain.usecase

import javax.inject.Inject
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

class CreateTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(title: String, description: String): Task {
        if (title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }

        val task =
                Task(
                        id = "",
                        title = title,
                        description = description,
                        isCompleted = false,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                )

        return repository.createTask(task)
    }
}
