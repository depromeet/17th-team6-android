package com.dpm.sixpack.data.source.remote.service.di

import com.dpm.sixpack.data.source.remote.service.AuthService
import com.dpm.sixpack.data.source.remote.service.FeedService
import com.dpm.sixpack.data.source.remote.service.FriendServiceApi
import com.dpm.sixpack.data.source.remote.service.RunningSessionServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideRunningSessionService(retrofit: Retrofit): RunningSessionServiceApi =
        retrofit.create(RunningSessionServiceApi::class.java)

//    @Provides
//    @Singleton
//    fun provideRunningSessionServiceApi(mock: MockRunningSessionServiceApi): RunningSessionServiceApi = mock

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService = retrofit.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideFriendService(retrofit: Retrofit): FriendServiceApi = retrofit.create(FriendServiceApi::class.java)
}
