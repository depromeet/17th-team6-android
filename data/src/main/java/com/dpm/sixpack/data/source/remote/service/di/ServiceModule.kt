package com.dpm.sixpack.data.source.remote.service.di

import com.dpm.sixpack.data.source.remote.di.AuthRetrofit
import com.dpm.sixpack.data.source.remote.di.RefreshRetrofit
import com.dpm.sixpack.data.source.remote.service.AuthService
import com.dpm.sixpack.data.source.remote.service.FeedService
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
    /**
     * 일반 API 호출용 AuthService (AuthInterceptor + TokenAuthenticator 포함)
     */
    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthService(
        @AuthRetrofit retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)

    /**
     * 토큰 갱신 전용 AuthService (Authenticator 제외, 데드락 방지)
     */
    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshAuthService(
        @RefreshRetrofit retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideRunningGoalService(
        @AuthRetrofit retrofit: Retrofit,
    ): RunningGoalService =
        retrofit.create(RunningGoalService::class.java)

    @Provides
    @Singleton
    fun provideRunningSessionService(
        @AuthRetrofit retrofit: Retrofit,
    ): RunningSessionService =
        retrofit.create(RunningSessionService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(
        @AuthRetrofit retrofit: Retrofit,
    ): FeedService = retrofit.create(FeedService::class.java)
}
