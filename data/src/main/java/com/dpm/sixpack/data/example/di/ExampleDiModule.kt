package com.dpm.sixpack.data.example.di

import com.dpm.sixpack.data.example.ExampleRepositoryImpl
import com.dpm.sixpack.domain.example.ExampleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExampleDiModule {
    @Binds
    @Singleton
    abstract fun bindExampleRepository(repository: ExampleRepositoryImpl): ExampleRepository
}
