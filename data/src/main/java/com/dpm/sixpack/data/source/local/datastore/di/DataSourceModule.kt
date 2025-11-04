package com.dpm.sixpack.data.source.local.datastore.di

import com.dpm.sixpack.data.source.local.datastore.UserPreferenceDataSourceImpl
import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.data.source.local.file.FileDataSourceImpl
import com.dpm.sixpack.data.source.local.file.api.FileDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataSourceModule {
    @Binds
    abstract fun bindsDataSource(dataSource: UserPreferenceDataSourceImpl): UserPreferenceDataSource

    @Binds
    abstract fun bindsFileDataSource(dataSource: FileDataSourceImpl): FileDataSource
}
