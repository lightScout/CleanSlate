package org.lightscout.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.lightscout.domain.usecase.CreateTaskUseCase
import org.lightscout.domain.usecase.DeleteTaskUseCase
import org.lightscout.domain.usecase.GetTaskByIdUseCase
import org.lightscout.domain.usecase.GetTasksUseCase
import org.lightscout.domain.usecase.UpdateTaskUseCase
import org.lightscout.presentation.tasklist.TaskListViewModel

@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {

    @Provides
    @ViewModelScoped
    fun provideTaskListViewModel(
            getTasksUseCase: GetTasksUseCase,
            getTaskByIdUseCase: GetTaskByIdUseCase,
            createTaskUseCase: CreateTaskUseCase,
            updateTaskUseCase: UpdateTaskUseCase,
            deleteTaskUseCase: DeleteTaskUseCase
    ): TaskListViewModel {
        return TaskListViewModel(
                getTasksUseCase = getTasksUseCase,
                getTaskByIdUseCase = getTaskByIdUseCase,
                createTaskUseCase = createTaskUseCase,
                updateTaskUseCase = updateTaskUseCase,
                deleteTaskUseCase = deleteTaskUseCase
        )
    }
}
