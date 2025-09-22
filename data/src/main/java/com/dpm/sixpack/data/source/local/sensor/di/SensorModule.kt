package com.dpm.sixpack.data.source.local.sensor.di

import android.content.Context
import android.hardware.SensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {
    @Singleton
    @Provides
    fun provideSensorManager(
        @ApplicationContext context: Context,
    ): SensorManager {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

        return sensorManager ?: throw IllegalStateException("SensorManager가 기기에 존재하지 않습니다.")
    }
}
