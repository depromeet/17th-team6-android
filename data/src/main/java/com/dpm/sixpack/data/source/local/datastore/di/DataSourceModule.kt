package com.dpm.sixpack.data.source.local.datastore.di

import com.dpm.sixpack.data.source.local.datastore.PreferenceDataSourceImpl
import com.dpm.sixpack.data.source.local.datastore.api.PreferenceDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataSourceModule {
    @Binds
    abstract fun bindsDataSource(
        dataSource: PreferenceDataSourceImpl,
    ): PreferenceDataSource
}
