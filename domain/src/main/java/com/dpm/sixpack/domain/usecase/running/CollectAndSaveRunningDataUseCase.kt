package com.dpm.sixpack.domain.usecase.running

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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectAndSaveRunningDataUseCase @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val sensorRepository: SensorRepository,
    private val runningSessionRepository: RunningSessionRepository,
    private val stateTracker: RunningStateTracker,
) {
    private val useCaseScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Coroutine Jobs
    private var timerJob: Job? = null
    private var locationJob: Job? = null
    private var stepJob: Job? = null

    // Tracker의 StateFlow를 그대로 외부에 노출
    val runningDataState: Flow<RealtimeRunningData?> = stateTracker.runningDataState

    init {
        // Tracker가 "첫 위치 받음" 이벤트를 보내면, 그때 타이머를 시작
        useCaseScope.launch {
            stateTracker.onFirstLocationReceived.collect {
                startTimer()
            }
        }

        //  1초마다 로컬DB 저장 수행
        useCaseScope.launch {
            stateTracker.runningDataState.filterNotNull().collect { data ->
                Timber.d("RealtimeRunningData: $data")
                runningSessionRepository.saveRealtimeDataOnLocal(data)
            }
        }
    }

    fun start() {
        if (timerJob?.isActive == true) return
        stateTracker.initStates()
        stateTracker.resume() // isPaused = false
        startDataCollectionJobs()
    }

    fun pause() {
        stateTracker.pause() // isPaused = true
        cancelAllJobs()
    }

    fun resume() {
        if (timerJob?.isActive == true) return
        stateTracker.resume()
        startDataCollectionJobs()

        startTimer()
    }

    fun stop() {
        cancelAllJobs()
        stateTracker.initStates()
    }

    fun close() {
        useCaseScope.cancel()
    }

    private fun startDataCollectionJobs() {
        startLocationCollection(useCaseScope)
        startStepCollection(useCaseScope)
    }

    private fun cancelAllJobs() {
        timerJob?.cancel()
        locationJob?.cancel()
        stepJob?.cancel()

        timerJob = null
        locationJob = null
        stepJob = null
    }

    // 타이머는 단순히 "Tick" 이벤트만 Tracker에게 보냄
    private fun startTimer() {
        if (timerJob?.isActive == true) return

        timerJob =
            useCaseScope.launch {
                while (isActive) {
                    delay(1000L)
                    stateTracker.processTimerTick()
                }
            }
    }

    // GPS 데이터를 받아서 Tracker에게 전달
    private fun startLocationCollection(scope: CoroutineScope) {
        locationJob =
            scope.launch {
                gpsRepository.locationFlow.collect { result ->
                    result.onSuccess { newLocation ->
                        stateTracker.processNewLocation(newLocation)
                    }
                }
            }
    }

    // 걸음 수 데이터를 받아서 Tracker에게 전달
    private fun startStepCollection(scope: CoroutineScope) {
        stepJob =
            scope.launch {
                sensorRepository.totalStep.collect { result ->
                    result.onSuccess { stepsSinceResume ->
                        stateTracker.processNewSteps(stepsSinceResume.toLong())
                    }
                }
            }
    }
}

sealed class SaveRealtimeRunningDataParam {
    data class LocalParam(
        val data: RealtimeRunningData,
    ) : SaveRealtimeRunningDataParam()

    data class SyncParam(
        val sessionId: Long,
        val isPaused: Boolean,
    ) : SaveRealtimeRunningDataParam()
}

sealed class SaveRealtimeRunningDataResult {
    data object LocalResult : SaveRealtimeRunningDataResult()

    data class SyncResult(
        val segmentId: Long = -1, // 구간 ID
        val savedCount: Int = 0, // 저장된 데이터 개수
    ) : SaveRealtimeRunningDataResult()
}
