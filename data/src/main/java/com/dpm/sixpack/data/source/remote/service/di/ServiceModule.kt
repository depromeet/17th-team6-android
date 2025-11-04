package com.dpm.sixpack.data.source.remote.service.di

import com.dpm.sixpack.data.source.remote.service.FeedService
import com.dpm.sixpack.data.source.remote.service.AuthService
import com.dpm.sixpack.data.source.remote.service.RunningGoalService
import com.dpm.sixpack.data.source.remote.service.RunningSessionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideRunningGoalService(retrofit: Retrofit): RunningGoalService =
        retrofit.create(RunningGoalService::class.java)

    @Provides
    @Singleton
    fun provideRunningSessionService(retrofit: Retrofit): RunningSessionService =
        retrofit.create(RunningSessionService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService = retrofit.create(FeedService::class.java)
}
