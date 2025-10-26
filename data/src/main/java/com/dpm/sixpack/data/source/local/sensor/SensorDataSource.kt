package com.dpm.sixpack.data.source.local.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.dpm.sixpack.core.network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorDataSource @Inject constructor(
    private val sensorManager: SensorManager,
    @ApplicationScope appScope: CoroutineScope,
) {
    val totalStepsFlow: Flow<Int> =
        callbackFlow {
            val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (stepCounterSensor == null) {
                Timber.w("Data : 걸음수 측정 센서(TYPE_STEP_COUNTER)를 찾을 수 없습니다.")
                close(IllegalStateException("걸음수 측정 센서를 찾을 수 없습니다."))
                return@callbackFlow
            }

            var initialSteps = -1

            val sensorEventListener =
                object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                            // 센서가 제공하는 기기 재부팅 후의 절대적인 총 걸음 수
                            val totalSteps = event.values[0].toInt()
                            if (initialSteps == -1) {
                                initialSteps = totalSteps
                            }
                            // 현재 총 걸음 수 - 구독 시작 시 걸음 수 = "이번 세션"의 걸음 수
                            trySend(totalSteps - initialSteps)
                        }
                    }

                    override fun onAccuracyChanged(
                        sensor: Sensor?,
                        accuracy: Int,
                    ) {
                    }
                }

            Timber.d("Data : SensorManager가 걸음수 수집을 시작합니다. (새 구독 발생)")
            sensorManager.registerListener(
                sensorEventListener,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI,
            )

            // 플로우가 닫힐 때 (구독이 취소될 때) 리스너를 해제
            awaitClose {
                Timber.d("Data : SensorManager가 걸음수 수집을 중단합니다. (구독 취소)")
                sensorManager.unregisterListener(sensorEventListener)
            }
        }.shareIn(
            scope = appScope, // 앱 생명주기를 따르는 스코프
            started = SharingStarted.WhileSubscribed(1000L), // 구독자가 없어진 후 3초간 유지
            replay = 1, // 새로운 구독자에게 최신 값 1개를 즉시 재전송
        )
}
