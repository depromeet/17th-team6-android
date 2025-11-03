package com.dpm.sixpack.data.repository.di

import com.dpm.sixpack.data.repository.AuthRepositoryImpl
import com.dpm.sixpack.data.repository.FileRepositoryImpl
import com.dpm.sixpack.data.repository.RunningSessionRepositoryImpl
import com.dpm.sixpack.data.repository.UserPreferenceRepositoryImpl
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.FileRepository
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
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(repositoryImpl: FileRepositoryImpl): FileRepository

    @Binds
    @Singleton
    abstract fun bindsRunningSessionRepository(repositoryImpl: RunningSessionRepositoryImpl): RunningSessionRepository

    @Binds
    @Singleton
    abstract fun bindsUserPreferenceRepository(repositoryImpl: UserPreferenceRepositoryImpl): UserPreferenceRepository
}
