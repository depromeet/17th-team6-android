package com.dpm.sixpack.presentation.map.navermap.di

import com.dpm.sixpack.presentation.map.navermap.AppInitializer
import com.dpm.sixpack.presentation.map.navermap.NaverMapInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/*
 * This Module is used to initialize the NaverMap SDK when use standard SDK, not 3rd compose library
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MapInitializerModule {

    @Binds
    @Singleton
    abstract fun bindMapSdkInitializer(
        initializer: NaverMapInitializer
    ): AppInitializer
}
