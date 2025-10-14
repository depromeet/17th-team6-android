package com.dpm.sixpack.data.repository.di

import com.dpm.sixpack.core.BuildConfig
import com.dpm.sixpack.data.repository.GpsRepositoryImpl
import com.dpm.sixpack.data.repository.SensorRepositoryImpl
import com.dpm.sixpack.data.repository.mock.MockSensorRepository
import com.dpm.sixpack.data.source.local.gps.LocationDataSource
import com.dpm.sixpack.data.source.local.sensor.SensorDataSource
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.repository.SensorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceDataRepositoryModule {
    @Provides
    @Singleton
    fun provideGpsRepository(locationDataSource: LocationDataSource): GpsRepository =
        GpsRepositoryImpl(locationDataSource)
//        if (BuildConfig.DEBUG) {
//            MockGpsRepository()
//        } else {
//            GpsRepositoryImpl(locationDataSource)
//        }

    @Provides
    @Singleton
    fun provideSensorRepository(sensorDataSource: SensorDataSource): SensorRepository =
//        SensorRepositoryImpl(sensorDataSource)
        if (BuildConfig.DEBUG) {
            MockSensorRepository()
        } else {
            SensorRepositoryImpl(sensorDataSource)
        }
}
