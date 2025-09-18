package com.dpm.sixpack.data.source.local.sensor.di

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class SensorDataSource @Inject constructor(
    private val sensorManager: SensorManager
) {
    fun getTotalStepsFlow(): Flow<Long> = callbackFlow {
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        var initialSteps = -1L

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    val totalSteps = event.values[0].toLong()
                    if (initialSteps == -1L) {
                        initialSteps = totalSteps
                    }
                    trySend(totalSteps - initialSteps)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }

        Timber.d("Data : SensorManager가 걸음수 수집을 시작합니다.")
        sensorManager.registerListener(
            sensorEventListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            Timber.d("Data : SensorManager가 걸음수 수집을 중단합니다.")
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
}
