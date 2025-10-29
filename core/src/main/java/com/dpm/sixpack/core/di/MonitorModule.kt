package com.dpm.sixpack.core.di

import com.dpm.sixpack.core.util.ConnectivityManagerNetworkMonitor
import com.dpm.sixpack.core.util.NetworkMonitor
import com.dpm.sixpack.core.util.TimeZoneBroadcastMonitor
import com.dpm.sixpack.core.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitorModule {
    @Binds
    abstract fun bindsNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor

    @Binds
    abstract fun bindsTimeZoneMonitor(timeZoneMonitor: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
