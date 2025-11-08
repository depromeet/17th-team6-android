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
import javax.inject.Singleton

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

    @Provides
    @Singleton
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
                addInterceptor(authInterceptor)
                if (DEBUG) addInterceptor(loggingInterceptor)
                authenticator(tokenAuthenticator)
            }.build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesDoRunRetrofit(
        okhttpCallFactory: dagger.Lazy<Call.Factory>,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()
}
