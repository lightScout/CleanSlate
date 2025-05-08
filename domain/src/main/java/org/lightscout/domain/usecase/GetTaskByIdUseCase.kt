package org.lightscout.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

class GetTaskByIdUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(id: String): Flow<Task> = repository.getTaskById(id)
}
