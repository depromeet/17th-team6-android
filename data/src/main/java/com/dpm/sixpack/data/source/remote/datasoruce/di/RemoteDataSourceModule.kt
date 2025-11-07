package com.dpm.sixpack.data.source.remote.datasoruce.di

import com.dpm.sixpack.data.source.remote.datasoruce.AuthDataSourceImpl
import com.dpm.sixpack.data.source.remote.datasoruce.api.AuthDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class RemoteDataSourceModule {
//    @Binds
//    abstract fun bindsAuthDataSource(dataSource: MockAuthDataSource): AuthDataSource

    // TODO: 서버 API 안정화 후 MockAuthDataSource → AuthDataSourceImpl로 변경
    @Binds
    abstract fun bindsAuthDataSource(dataSource: AuthDataSourceImpl): AuthDataSource
}
