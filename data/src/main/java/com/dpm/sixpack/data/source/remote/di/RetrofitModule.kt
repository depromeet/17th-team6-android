package com.dpm.sixpack.data.source.remote.di

import com.dpm.sixpack.core.BuildConfig
import com.dpm.sixpack.core.BuildConfig.DEBUG
import com.dpm.sixpack.data.source.remote.interceptor.AuthInterceptor
import com.dpm.sixpack.data.source.remote.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 일반 API 호출용 Retrofit (AuthInterceptor + TokenAuthenticator 포함)
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

/**
 * 토큰 갱신 전용 Retrofit (Authenticator 제외, 데드락 방지)
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshRetrofit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun providesJson(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
            prettyPrint = true
        }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * 일반 API 호출용 OkHttpClient (AuthInterceptor + TokenAuthenticator 포함)
     */
    @Provides
    @Singleton
    @AuthRetrofit
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): Call.Factory =
        OkHttpClient
            .Builder()
            .apply {
                connectTimeout(10, TimeUnit.SECONDS)
                writeTimeout(10, TimeUnit.SECONDS)
                readTimeout(10, TimeUnit.SECONDS)
                if (DEBUG) addInterceptor(loggingInterceptor)
                addInterceptor(authInterceptor)
                authenticator(tokenAuthenticator)
            }.build()

    /**
     * 토큰 갱신 전용 OkHttpClient (Authenticator 제외, 데드락 방지)
     * AuthInterceptor는 포함하지 않음 (토큰 갱신 API는 Authorization 헤더 불필요)
     */
    @Provides
    @Singleton
    @RefreshRetrofit
    fun providesRefreshOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): Call.Factory =
        OkHttpClient
            .Builder()
            .apply {
                connectTimeout(10, TimeUnit.SECONDS)
                writeTimeout(10, TimeUnit.SECONDS)
                readTimeout(10, TimeUnit.SECONDS)
                if (DEBUG) addInterceptor(loggingInterceptor)
                // AuthInterceptor와 TokenAuthenticator를 추가하지 않음
            }.build()

    /**
     * 일반 API 호출용 Retrofit
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @AuthRetrofit
    fun providesDoRunRetrofit(
        @AuthRetrofit okhttpCallFactory: dagger.Lazy<Call.Factory>,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()

    /**
     * 토큰 갱신 전용 Retrofit (데드락 방지)
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @RefreshRetrofit
    fun providesRefreshRetrofit(
        @RefreshRetrofit okhttpCallFactory: dagger.Lazy<Call.Factory>,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()

    /**
     * Qualifier 없는 기본 Call.Factory (이미지 로더 등에서 사용)
     * @AuthRetrofit과 동일한 인스턴스 제공
     */
    @Provides
    @Singleton
    fun providesDefaultOkHttpClient(
        @AuthRetrofit callFactory: Call.Factory,
    ): Call.Factory = callFactory
}
