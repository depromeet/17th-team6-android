package com.dpm.sixpack.domain.usecase

import android.location.Location
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.repository.SensorRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealtimeRunningDataUseCase
    @Inject
    constructor(
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

            val distanceAccumulatorFlow: Flow<Pair<Location?, Float>> =
                gpsRepository
                    .getLocationFlow()
                    .scan(Pair<Location?, Float>(null, 0f)) { accumulator, locationResult ->
                        if (locationResult is DoRunResult.Failure) {
                            return@scan accumulator
                        }
                        val newLocation = (locationResult as DoRunResult.Success).data
                        val lastLocation = accumulator.first
                        val totalDistance = accumulator.second

                        val newTotalDistance =
                            if (lastLocation != null) {
                                totalDistance + lastLocation.distanceTo(newLocation)
                            } else {
                                totalDistance
                            }

                        return@scan Pair(newLocation, newTotalDistance)
                    }

            return combine(
                distanceAccumulatorFlow,
                sensorRepository
                    .getTotalStep()
                    // 센서감지안되면 시작안하는거 방지용
                    .onStart { emit(DoRunResult.Success(0)) },
                durationFlow,
            ) { distanceData, stepsResult, duration ->

                if (stepsResult is DoRunResult.Failure) {
                    return@combine stepsResult
                }

                val currentLocation = distanceData.first
                val totalDistance = distanceData.second
                val totalSteps = (stepsResult as DoRunResult.Success).data

                val speed = currentLocation?.speed ?: 0f

                val pace = if (speed > 0.3f) (1000f / speed).toInt() else 0

                val durationInMinutes = if (duration > 0) duration / 60.0 else 0.0
                val cadence = if (durationInMinutes > 0) (totalSteps / durationInMinutes).toInt() else 0

                val realtimeData =
                    RealtimeRunningData(
                        latitude = currentLocation?.latitude ?: 0.0,
                        longitude = currentLocation?.longitude ?: 0.0,
                        altitude = currentLocation?.altitude ?: 0.0,
                        speed = speed,
                        pace = pace,
                        cadence = cadence,
                        totalDistanceMeter = totalDistance,
                        duration = duration,
                        timestamp = System.currentTimeMillis(),
                    )
                Timber.d("실시간 데이터: $realtimeData")
                DoRunResult.Success(realtimeData)
            }.onStart {
                emit(DoRunResult.Success(RealtimeRunningData()))
            }
        }
    }
