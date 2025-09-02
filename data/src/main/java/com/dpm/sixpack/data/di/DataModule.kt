package com.dpm.sixpack.data.di

import com.dpm.sixpack.data.util.ConnectivityManagerNetworkMonitor
import com.dpm.sixpack.data.util.NetworkMonitor
import com.dpm.sixpack.data.util.TimeZoneBroadcastMonitor
import com.dpm.sixpack.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsTimeZoneMonitor(
        timeZoneMonitor: TimeZoneBroadcastMonitor
    ): TimeZoneMonitor
}
