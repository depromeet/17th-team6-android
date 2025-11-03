package com.dpm.sixpack.data.source.remote.service.di

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
    fun provideRunningSessionService(retrofit: Retrofit): RunningSessionServiceApi =
        retrofit.create(RunningSessionServiceApi::class.java)

    @Provides
    @Singleton
    fun provideFriendService(retrofit: Retrofit): FriendServiceApi = retrofit.create(FriendServiceApi::class.java)
}
