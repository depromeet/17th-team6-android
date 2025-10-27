package com.dpm.sixpack.domain.usecase

import android.location.Location
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.SensorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

class CollectAndSaveRunningDataUseCase @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val sensorRepository: SensorRepository,
    private val runningSessionRepository: RunningSessionRepository,
) {
    private val useCaseScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // --- 내부 상태 관리 변수 ---
    private var isPaused = true

    private var durationInSeconds: Int = 0
    private var lastLocation: Location? = null
    private var totalDistance: Double = 0.0
    private var stepsBeforePause: Long = 0L
    private var currentSteps: Long = 0L

    private var paceAverage: Int = 0
    private var cadence: Int = 0

    // Coroutine Jobs
    private var timerJob: Job? = null
    private var locationJob: Job? = null
    private var stepJob: Job? = null

    // 외부 공개 상태
    private val _runningDataState = MutableStateFlow<RealtimeRunningData?>(null)
    val runningDataState: Flow<RealtimeRunningData?> = _runningDataState.asStateFlow()

    /**
     * 러닝 데이터 수집 및 저장을 시작합니다.
     */
    fun start() {
        if (timerJob?.isActive == true) return // 이미 실행 중이면 무시

        initStates()
        isPaused = false
        startJobs()
    }

    /**
     * 일시정지. 데이터 수집을 중단합니다.
     */
    fun pause() {
        isPaused = true
        stepsBeforePause = currentSteps
        cancelJobs()
    }

    /**
     * 다시 시작. 데이터 수집을 재개합니다.
     */
    fun resume() {
        if (timerJob?.isActive == true) return // 이미 실행 중이면 무시
        isPaused = false
        startJobs()
    }

    /**
     * 중지. 모든 데이터 수집을 중단하고 상태를 초기화합니다.
     */
    fun stop() {
        cancelJobs()
        isPaused = true
        initStates()
        // TODO: 세션 종료 정보를 Repository를 통해 저장하는 로직 추가
    }

    /**
     * (유지) UseCase가 종료될 때 외부에서 Scope를 정리(cancel)하기 위한 메서드
     */
    fun close() {
        useCaseScope.cancel()
    }

    private fun startJobs() {
//        startTimer(useCaseScope)
        startLocationCollection(useCaseScope)
        startStepCollection(useCaseScope)
    }

    private fun cancelJobs() {
        timerJob?.cancel()
        locationJob?.cancel()
        stepJob?.cancel()
        timerJob = null
        locationJob = null
        stepJob = null
    }

    private fun initStates() {
        durationInSeconds = 0
        lastLocation = null
        totalDistance = 0.0
        stepsBeforePause = 0L
        currentSteps = 0L

        paceAverage = 0
        cadence = 0

        _runningDataState.value = null
    }

    // 시간 측정 및 주기적 계산
    private fun startTimer(scope: CoroutineScope) {
        timerJob =
            scope.launch {
                while (isActive) {
                    delay(1000L)
                    durationInSeconds += 1

                    if (durationInSeconds % CALCULATE_PERIOD == 0 || durationInSeconds == 1) {
                        paceAverage = calculateAvgPace(totalDistance, durationInSeconds)
                        cadence = calculateAvgCadence(currentSteps, durationInSeconds)
                    }
                    postCurrentRunningDataState()
                }
            }
    }

    // 1초마다 RealTime 데이터 업데이트 및 저장
    private fun postCurrentRunningDataState() {
        lastLocation?.let {
            val roundedDistance = (round(totalDistance / 10.0) * 10).toInt()

            val data =
                RealtimeRunningData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude,
                    speed = it.speed,
                    pace = paceAverage,
                    cadence = cadence,
                    totalDistanceMeter = roundedDistance,
                    duration = durationInSeconds,
                    timestamp = System.currentTimeMillis(),
                )

            _runningDataState.value = data
        }
    }

    /**
     * GPS 데이터 구독하여 거리 업데이트
     */
    private fun startLocationCollection(scope: CoroutineScope) {
        locationJob =
            scope.launch {
                gpsRepository.locationFlow.collect { result ->
                    result.onSuccess { newLocation ->
                        if (!isPaused) {
                            val isFirstLocation = (lastLocation == null)

                            lastLocation?.let {
                                totalDistance += it.distanceTo(newLocation)
                            }
                            lastLocation = newLocation

                            if (isFirstLocation) {
                                startTimer(useCaseScope)
                            }
                        }
                    }
                }
            }
    }

    /**
     * 걸음 수 데이터 구독하여 걸음 수를 누적 업데이트
     */
    private fun startStepCollection(scope: CoroutineScope) {
        stepJob =
            scope.launch {
                sensorRepository.totalStep.collect { result ->
                    result.onSuccess { stepsSinceResume ->
                        if (!isPaused) {
                            currentSteps = stepsBeforePause + stepsSinceResume.toLong()
                        }
                    }
                }
            }
    }

    // --- 계산 헬퍼 함수 ---
    private fun calculateAvgPace(
        totalDistanceInMeters: Double,
        durationInSeconds: Int,
    ): Int {
        if (durationInSeconds <= 0) return 0
        val speedInMps = totalDistanceInMeters / durationInSeconds
        if (speedInMps <= 0) return 0
        val secondsPerKilometer = 1000.0 / speedInMps
        return secondsPerKilometer.toInt()
    }

    private fun calculateAvgCadence(
        totalSteps: Long,
        durationInSeconds: Int,
    ): Int {
        if (durationInSeconds <= 0L) {
            return 0
        }
        val durationInMinutes = durationInSeconds / 60.0
        if (durationInMinutes == 0.0) return 0
        return (totalSteps / durationInMinutes).toInt()
    }

    companion object {
        private const val CALCULATE_PERIOD = 3
    }
}
