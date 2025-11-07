package com.dpm.sixpack.data.repository.di

import com.dpm.sixpack.data.repository.AuthRepositoryImpl
import com.dpm.sixpack.data.repository.FeedRepositoryImpl
import com.dpm.sixpack.data.repository.FileRepositoryImpl
import com.dpm.sixpack.data.repository.FriendRepositoryImpl
import com.dpm.sixpack.data.repository.MockPostRepository
import com.dpm.sixpack.data.repository.RunningSessionRepositoryImpl
import com.dpm.sixpack.data.repository.UserPreferenceRepositoryImpl
import com.dpm.sixpack.data.repository.UserRepositoryImpl
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FileRepository
import com.dpm.sixpack.domain.repository.FriendRepository
import com.dpm.sixpack.domain.repository.PostRepository
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.repository.UserRepository
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
    abstract fun bindRunningSessionRepository(repositoryImpl: RunningSessionRepositoryImpl): RunningSessionRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferenceRepository(repositoryImpl: UserPreferenceRepositoryImpl): UserPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(mockPostRepository: MockPostRepository): PostRepository

    @Binds
    @Singleton
    abstract fun bindFriendRepository(repositoryImpl: FriendRepositoryImpl): FriendRepository

    @Binds
    abstract fun bindFeedRepository(feedRepositoryImpl: FeedRepositoryImpl): FeedRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}
