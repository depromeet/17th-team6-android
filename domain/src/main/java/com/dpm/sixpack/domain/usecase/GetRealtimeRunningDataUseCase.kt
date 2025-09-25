package com.dpm.sixpack.domain.usecase

import android.location.Location
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.repository.SensorRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealtimeRunningDataUseCase @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val sensorRepository: SensorRepository,
) {
    operator fun invoke(): Flow<DoRunResult<RealtimeRunningData>> {
        val durationFlow =
            flow {
                var duration = 0
                while (true) {
                    emit(duration)
                    delay(1000L)
                    duration++
                }
            }

        // (위치, 거리)
        val distanceAccumulatorFlow: Flow<Pair<Location?, Float>> =
            flow {
                var totalDistance = 0f
                var lastLocation: Location? = null
                gpsRepository
                    .getLocationFlow()
                    .collect { locationResult ->
                        locationResult
                            .onSuccess { newLocation ->
                                lastLocation?.let {
                                    totalDistance += it.distanceTo(newLocation)
                                    emit(Pair(newLocation, totalDistance))
                                }
                                lastLocation = newLocation
                            }.onError { exception ->
                                Timber.w(exception, "기기 위치 데이터 수신 실패")
                            }
                    }
            }

        val stepFlow: Flow<DoRunResult<Int>> =
            sensorRepository
                .getTotalStep()
                .onEach { result ->
                    // onEach는 Flow의 각 아이템에 대해 "액션"만 수행하고 아이템은 그대로 통과
                    result
                        .onSuccess { steps ->
                            Timber.d("걸음 수 데이터 수신 성공: $steps")
                        }.onError { exception ->
                            Timber.w(exception, "걸음 수 데이터 수신 실패")
                        }
                }.catch { e ->
                    val exception = DoRunException.DataError(e.message.toString())
                    emit(DoRunResult.Failure(exception))
                }.onStart {
                    // 센서감지안되면 시작안하는거 방지용
                    emit(DoRunResult.Success(0))
                }

        return combine(
            distanceAccumulatorFlow,
            stepFlow,
            durationFlow,
        ) { distanceData, stepsResult, duration ->

            val currentLocation = distanceData.first
            val totalDistance = distanceData.second
            val totalSteps = (stepsResult as DoRunResult.Success).data

            val speed = currentLocation?.speed ?: 0f

            val totalDistanceInKm = totalDistance / 1000.0

            val avgPace =
                if (totalDistanceInKm > 0) {
                    (duration / totalDistanceInKm).toInt()
                } else {
                    0
                }

            val durationInMinutes = if (duration > 0) duration / 60.0 else 0.0
            val cadence = if (durationInMinutes > 0) (totalSteps / durationInMinutes).toInt() else 0

            val realtimeData =
                RealtimeRunningData(
                    latitude = currentLocation?.latitude ?: 0.0,
                    longitude = currentLocation?.longitude ?: 0.0,
                    altitude = currentLocation?.altitude ?: 0.0,
                    speed = speed,
                    pace = avgPace,
                    cadence = cadence,
                    totalDistanceMeter = totalDistance.toInt(),
                    duration = duration,
                    timestamp = System.currentTimeMillis(),
                )
            Timber.d("실시간 데이터: $realtimeData")
            DoRunResult.Success(realtimeData)
        }
    }
}
