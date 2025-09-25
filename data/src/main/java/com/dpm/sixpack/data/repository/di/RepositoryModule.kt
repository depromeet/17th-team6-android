package com.dpm.sixpack.data.repository.di

import com.dpm.sixpack.data.repository.MockRunningGoalRepositoryImpl
import com.dpm.sixpack.data.repository.RunningRepositoryImpl
import com.dpm.sixpack.data.repository.RunningSessionRepositoryImpl
import com.dpm.sixpack.data.repository.UserPreferenceRepositoryImpl
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.repository.RunningRepository
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
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
    abstract fun bindRunningRepository(runningRepository: RunningRepositoryImpl): RunningRepository

    @Binds
    @Singleton
    abstract fun bindsRunningGoalRepository(repositoryImpl: MockRunningGoalRepositoryImpl): RunningGoalRepository

    @Binds
    @Singleton
    abstract fun bindsRunningSessionRepository(repositoryImpl: RunningSessionRepositoryImpl): RunningSessionRepository

    @Binds
    @Singleton
    abstract fun bindsUserPreferenceRepository(repositoryImpl: UserPreferenceRepositoryImpl): UserPreferenceRepository

//    @Binds
//    @Singleton
//    abstract fun bindsGpsRepository(repositoryImpl: GpsRepositoryImpl): GpsRepository
//
//    @Binds
//    @Singleton
//    abstract fun bindsSensorRepository(repositoryImpl: SensorRepositoryImpl): SensorRepository
}
