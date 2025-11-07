package com.dpm.sixpack.data.source.remote.service.di

import com.dpm.sixpack.data.source.remote.service.AuthService
import com.dpm.sixpack.data.source.remote.service.FeedService
import com.dpm.sixpack.data.source.remote.service.MockFeedService
import com.dpm.sixpack.data.source.remote.service.MockRunningSessionServiceApi
import com.dpm.sixpack.data.source.remote.service.RunningSessionServiceApi
import com.dpm.sixpack.data.source.remote.service.UserService
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
    fun provideUserService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideRunningSessionServiceApi(retrofit: Retrofit): RunningSessionServiceApi =
        retrofit.create(RunningSessionServiceApi::class.java)

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService = retrofit.create(FeedService::class.java)

    // region 모킹 서비스.

//    @Provides
//    @Singleton
//    fun provideRunningSessionServiceApi(mock: MockRunningSessionServiceApi): RunningSessionServiceApi =
//        mock
//
//    @Provides
//    @Singleton
//    fun provideFeedService(mock: MockFeedService): FeedService = mock

    // endregion
}
