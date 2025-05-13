package org.lightscout.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.lightscout.domain.model.Task
import org.lightscout.domain.repository.TaskRepository
import org.lightscout.domain.usecase.CreateTaskUseCase
import org.lightscout.domain.usecase.DeleteTaskUseCase
import org.lightscout.domain.usecase.GetTaskByIdUseCase
import org.lightscout.domain.usecase.GetTasksUseCase
import org.lightscout.domain.usecase.UpdateTaskUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideTaskRepository(): TaskRepository {
        return object : TaskRepository {
            private val tasks =
                    mutableListOf(
                            Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890),
                            Task("2", "Task 2", "Description 2", true, 1234567890, 1234567890)
                    )

            override fun getTasks(): Flow<List<Task>> = flowOf(tasks)

            override fun getTaskById(id: String): Flow<Task> {
                val task =
                        tasks.find { it.id == id }
                                ?: throw IllegalArgumentException("Task not found with id: $id")
                return flowOf(task)
            }

            override suspend fun createTask(task: Task): Task {
                val newTask =
                        task.copy(
                                id = task.id.ifEmpty { java.util.UUID.randomUUID().toString() },
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                        )
                tasks.add(newTask)
                return newTask
            }

            override suspend fun updateTask(task: Task): Task {
                val index = tasks.indexOfFirst { it.id == task.id }
                if (index == -1) {
                    throw IllegalArgumentException("Task not found with id: ${task.id}")
                }
                val updatedTask = task.copy(updatedAt = System.currentTimeMillis())
                tasks[index] = updatedTask
                return updatedTask
            }

            override suspend fun deleteTask(id: String) {
                val index = tasks.indexOfFirst { it.id == id }
                if (index != -1) {
                    tasks.removeAt(index)
                }
            }
        }
    }

    @Provides
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

    @Provides
    fun provideGetTaskByIdUseCase(repository: TaskRepository): GetTaskByIdUseCase {
        return GetTaskByIdUseCase(repository)
    }

    @Provides
    fun provideCreateTaskUseCase(repository: TaskRepository): CreateTaskUseCase {
        return CreateTaskUseCase(repository)
    }

    @Provides
    fun provideUpdateTaskUseCase(repository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(repository)
    }

    @Provides
    fun provideDeleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }
}
