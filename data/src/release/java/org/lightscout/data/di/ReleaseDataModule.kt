package org.lightscout.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.lightscout.data.repository.TaskRepositoryImpl
import org.lightscout.domain.repository.TaskRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class ReleaseDataModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository
}
