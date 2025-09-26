package com.dpm.sixpack.runningservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.dpm.sixpack.core.util.TimeUtil
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.usecase.GetGpsDataUseCase
import com.dpm.sixpack.domain.usecase.GetStepCountUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class RunningService : LifecycleService() {
    @Inject
    lateinit var getGpsDataUseCase: GetGpsDataUseCase

    @Inject
    lateinit var getStepCountUseCase: GetStepCountUseCase

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private val binder = RunningBinder()

    private lateinit var notificationManager: NotificationManager

    // 외부 공개 상태
    private val _runningDataState = MutableStateFlow<RealtimeRunningData?>(null)
    val runningDataState = _runningDataState.asStateFlow()

    // --- 내부 상태 관리 변수 ---
    private var isServiceRunning = false
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

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        intent?.action?.let { action ->
            when (action) {
                RunningActions.START_OR_RESUME -> {
                    startOrResumeService()
                }

                RunningActions.PAUSE -> {
                    pauseService()
                }

                RunningActions.STOP -> {
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    private fun startOrResumeService() {
        if (!isServiceRunning) {
            isServiceRunning = true
            startService()
        } else {
            resumeService()
        }
    }

    private fun startService() {
        isPaused = false
        initStates()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        startJobs()
    }

    private fun resumeService() {
        if (isPaused) {
            isPaused = false
            startJobs()
        }
    }

    private fun pauseService() {
        isPaused = true
        stepsBeforePause = currentSteps
        cancelJobs()
    }

    private fun stopService() {
        isServiceRunning = false
        isPaused = true
        cancelJobs()
        initStates()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun initStates() {
        durationInSeconds = 0
        lastLocation = null
        totalDistance = 0.0
        paceAverage = 0
        cadence = 0
        stepsBeforePause = 0L
        currentSteps = 0L
        _runningDataState.value = null
    }

    private fun startJobs() {
        startTimer()
        startLocationCollection()
        startStepCollection()
    }

    private fun cancelJobs() {
        timerJob?.cancel()
        locationJob?.cancel()
        stepJob?.cancel()
    }

    // 시간 측정
    private fun startTimer() {
        timerJob =
            lifecycleScope.launch {
                while (isActive) {
                    durationInSeconds += 1
                    delay(1000L)

                    if (durationInSeconds % CALCULATE_PERIOD == 0 || durationInSeconds == 1) {
                        paceAverage = calculateAvgPace(totalDistance, durationInSeconds)
                        cadence = calculateAvgCadence(currentSteps, durationInSeconds)
                    }
                    postCurrentRunningDataState()

                    updateNotification(durationInSeconds)
                }
            }
    }

    // 1초마다 RealTime 데이터 업데이트
    private fun postCurrentRunningDataState() {
        lastLocation?.let {
            val roundedDistance = (round(totalDistance / 10.0) * 10).toInt()

            _runningDataState.value =
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
        }
    }

    /**
     * GPS 데이터 구독하여 거리만 업데이트
     */
    private fun startLocationCollection() {
        locationJob =
            lifecycleScope.launch {
                getGpsDataUseCase().collect { result ->
                    result.onSuccess { newLocation ->
                        if (!isPaused) {
                            lastLocation?.let {
                                totalDistance += it.distanceTo(newLocation)
                            }
                            lastLocation = newLocation
                        }
                    }
                }
            }
    }

    /**
     * 걸음 수 데이터 구독하여 걸음 수를 누적 업데이트
     */
    private fun startStepCollection() {
        stepJob =
            lifecycleScope.launch {
                getStepCountUseCase().collect { result ->
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
        // 거리가 10m 미만이거나 시간이 0이면 페이스 계산 의미 없음
        if (durationInSeconds <= 0) {
            return 0
        }

        // (거리 m) / (시간 s) = 초당 미터
        val speedInMps = totalDistanceInMeters / durationInSeconds
        if (speedInMps <= 0) return 0

        // 1000m / (초당 가는 미터) = 초/km
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
        return (totalSteps / durationInMinutes).toInt()
    }

    // --- Notification ---

    private fun updateNotification(durationInSeconds: Int) {
        val notification =
            baseNotificationBuilder
                .setContentText(TimeUtil.formatMillisWithDuration(durationInSeconds * 1000L))
                .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW,
            )
        notificationManager.createNotificationChannel(channel)
    }

    inner class RunningBinder : Binder() {
        fun getService(): RunningService = this@RunningService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    companion object {
        private const val CALCULATE_PERIOD = 3
    }
}
