package com.dpm.sixpack.data.source.remote.repository.di

import com.dpm.sixpack.data.source.remote.repository.RunningGoalRepositoryImpl
import com.dpm.sixpack.data.source.remote.repository.RunningSessionRepositoryImpl
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsRunningGoalRepository(repositoryImpl: RunningGoalRepositoryImpl): RunningGoalRepository

    @Binds
    @Singleton
    abstract fun bindsRunningSessionRepository(repositoryImpl: RunningSessionRepositoryImpl): RunningSessionRepository
}
