package com.dpm.sixpack.core.network.di

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.util.DebugLogger
import com.dpm.sixpack.core.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun imageLoader(
        okHttpCallFactory: dagger.Lazy<okhttp3.Call.Factory>,
        @ApplicationContext application: Context,
    ): ImageLoader =
        ImageLoader
            .Builder(application)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = { okHttpCallFactory.get() },
                    ),
                )
            }.apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }.build()
}
