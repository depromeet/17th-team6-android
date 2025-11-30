package com.dpm.sixpack.data.repository.di

import com.dpm.sixpack.data.repository.GpsRepositoryImpl
import com.dpm.sixpack.data.repository.SensorRepositoryImpl
import com.dpm.sixpack.data.source.local.gps.GpsDataSource
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
    fun provideGpsRepository(gpsDataSource: GpsDataSource): GpsRepository = GpsRepositoryImpl(gpsDataSource)
    // Mock 데이터 필요시 아래 주석 사용
//        if (BuildConfig.DEBUG) {
//            MockGpsRepository()
//        } else {
//            GpsRepositoryImpl(locationDataSource)
//        }

    @Provides
    @Singleton
    fun provideSensorRepository(sensorDataSource: SensorDataSource): SensorRepository =
        SensorRepositoryImpl(sensorDataSource)
    // Mock 데이터 필요시 아래 주석 사용
//        if (BuildConfig.DEBUG) {
//            MockSensorRepository()
//        } else {
//            SensorRepositoryImpl(sensorDataSource)
//        }
}
