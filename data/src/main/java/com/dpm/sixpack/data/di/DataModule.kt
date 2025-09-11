package com.dpm.sixpack.data.di

import com.dpm.sixpack.data.local.running.RunningRepositoryImpl
import com.dpm.sixpack.data.local.running.RunningServiceRepositoryImpl
import com.dpm.sixpack.data.util.ConnectivityManagerNetworkMonitor
import com.dpm.sixpack.data.util.NetworkMonitor
import com.dpm.sixpack.data.util.TimeZoneBroadcastMonitor
import com.dpm.sixpack.data.util.TimeZoneMonitor
import com.dpm.sixpack.domain.running.RunningRepository
import com.dpm.sixpack.domain.running.RunningServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor

    @Binds
    internal abstract fun bindsTimeZoneMonitor(timeZoneMonitor: TimeZoneBroadcastMonitor): TimeZoneMonitor

    @Binds
    @Singleton
    abstract fun bindRunningRepository(runningRepository: RunningRepositoryImpl): RunningRepository

    @Binds
    @Singleton
    abstract fun bindRunningServiceRepository(
        runningServiceRepository: RunningServiceRepositoryImpl
    ): RunningServiceRepository
}
