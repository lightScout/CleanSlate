package org.lightscout.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository

class GetTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getTasks()
}
