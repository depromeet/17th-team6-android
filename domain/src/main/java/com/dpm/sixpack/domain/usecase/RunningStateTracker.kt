package com.dpm.sixpack.domain.usecase

import android.location.Location
import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RealtimeRunningData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class RunningStateTracker @Inject constructor() {
    private var isPaused = true

    private var durationInSeconds: Int = 0
    private var lastLocation: Location? = null
    private var totalDistance: Double = 0.0
    private var stepsBeforePause: Long = 0L
    private var currentSteps: Long = 0L

    // 평균값
    private var avgPace: Int = 0
    private var avgCadence: Int = 0

    // 최대값
    private var maxPace: MaxPaceData = MaxPaceData.default
    private var maxCadence: Int = 0

    // --- 외부 공개 상태 ---
    private val _runningDataState = MutableStateFlow<RealtimeRunningData?>(null)
    val runningDataState: Flow<RealtimeRunningData?> = _runningDataState.asStateFlow()

    // 첫 GPS 수신 시 타이머 시작을 요청하기 위한 이벤트 플로우
    private val _onFirstLocationReceived = MutableSharedFlow<Unit>(replay = 1)
    val onFirstLocationReceived: Flow<Unit> = _onFirstLocationReceived.asSharedFlow()

    fun initStates() {
        isPaused = true
        durationInSeconds = 0
        lastLocation = null
        totalDistance = 0.0
        stepsBeforePause = 0L
        currentSteps = 0L
        avgPace = 0
        avgCadence = 0
        maxPace = MaxPaceData.default
        maxCadence = 0
        _runningDataState.value = null
        _onFirstLocationReceived.resetReplayCache()
    }

    fun pause() {
        isPaused = true
        stepsBeforePause = currentSteps
    }

    fun resume() {
        isPaused = false
    }

    /**
     * 1초 타이머가 울릴 때마다 UseCase에 의해 호출
     */
    fun processTimerTick() {
        if (isPaused) return

        durationInSeconds += 1

        if (durationInSeconds % CALCULATE_PERIOD == 0 || durationInSeconds == 1) {
            avgPace = calculateAvgPace(totalDistance, durationInSeconds)
            avgCadence = calculateAvgCadence(currentSteps, durationInSeconds)
        }

        postCurrentRunningDataState()
    }

    /**
     * 새 GPS 위치를 받을 때마다 UseCase에 의해 호출
     */
    fun processNewLocation(newLocation: Location) {
        if (isPaused) return

        val isFirstLocation = (lastLocation == null)

        lastLocation?.let {
            totalDistance += it.distanceTo(newLocation)
        }
        lastLocation = newLocation

        // 첫 번째 위치를 받았다면, UseCase에게 타이머 시작을 알림
        if (isFirstLocation) {
            _onFirstLocationReceived.tryEmit(Unit)
        }
    }

    /**
     * 새 걸음 수를 받을 때마다 UseCase에 의해 호출
     */
    fun processNewSteps(stepsSinceResume: Long) {
        if (isPaused) return
        currentSteps = stepsBeforePause + stepsSinceResume
    }

    /**
     * 현재 누적된 상태를 기반으로 RealtimeRunningData 객체를 생성하고 발행
     */
    private fun postCurrentRunningDataState() {
        lastLocation?.let {
            val roundedDistance = (round(totalDistance / 10.0) * 10).toInt()

            val data =
                RealtimeRunningData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude,
                    speed = it.speed.toDouble(),
                    avgPace = avgPace,
                    maxPace = maxPace,
                    avgCadence = avgCadence,
                    maxCadence = maxCadence,
                    distanceInMeter = roundedDistance,
                    durationInSec = durationInSeconds,
                    timestamp = System.currentTimeMillis(),
                )

            _runningDataState.value = data
        }
    }

    /**
     * 평균 페이스를 계산하고, 최대 페이스를 갱신
     */
    private fun calculateAvgPace(
        totalDistanceInMeters: Double,
        durationInSeconds: Int,
    ): Int {
        if (durationInSeconds <= 0) return 0

        val speedInMps = totalDistanceInMeters / durationInSeconds
        if (speedInMps <= 0) return 0
        val secondsPerKilometer = 1000.0 / speedInMps

        val newPace = secondsPerKilometer.toInt()

        // 최대 페이스 갱신
        if (newPace > 0 && lastLocation != null) {
            val maxPaceValue = if (maxPace.value == -1) newPace else minOf(maxPace.value, newPace)

            maxPace =
                MaxPaceData(
                    value = maxPaceValue,
                    latitude = lastLocation!!.latitude,
                    longitude = lastLocation!!.longitude,
                )
        }

        return newPace
    }

    /**
     * 평균 케이던스를 계산하고, 최대 케이던스를 갱신
     */
    private fun calculateAvgCadence(
        totalSteps: Long,
        durationInSeconds: Int,
    ): Int {
        if (durationInSeconds <= 0L) {
            return 0
        }
        val durationInMinutes = durationInSeconds / 60.0
        if (durationInMinutes == 0.0) return 0

        val newCadence = (totalSteps / durationInMinutes).toInt()
        maxCadence = maxOf(maxCadence, newCadence)
        return newCadence
    }

    companion object {
        private const val CALCULATE_PERIOD = 3
    }
}
