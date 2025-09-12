package com.dpm.sixpack.runningservice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.dpm.sixpack.core.permission.PermissionUtil
import com.dpm.sixpack.core.permission.SixPackPermissions
import com.dpm.sixpack.core.util.TimeUtil
import com.dpm.sixpack.domain.model.RunningState
import com.dpm.sixpack.domain.running.RunningActions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RunningService : LifecycleService(), SensorEventListener {

    private val binder = RunningBinder()

    inner class RunningBinder : Binder() {
        fun getService(): RunningService = this@RunningService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    // 거리 계산을 위한 변수
    private var lastLocation: Location? = null
    private var totalDistance = 0.0

    // 페이스 계산을 위한 변수
    private var paceInMoment: Double = 0.0
    private var paceAverage: Double = 0.0
    private var timeOfLastPaceCalculate = 0L
    private var distanceAtLastPaceCalculation = 0.0

    // 케이던스 계산 위한 변수
    private var cadence = 0
    private var timeOfLastCadenceCalc = 0L
    private var stepsAtLastCadenceCalc = 0L
    private var currentSteps = 0L

    private lateinit var notificationManager: NotificationManager

    private val _runningDataState = MutableStateFlow(RunningState())
    val runningDataState = _runningDataState.asStateFlow()

    private var initialSteps = -1L
    private var timerJob: Job? = null
    private var startTimeInMillis: Long = 0L

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                RunningActions.START_OR_RESUME -> {
                    startForegroundService()
                }

                RunningActions.PAUSE -> {
                    // TODO SK: 일시정지 로직
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

    private fun initStates() {
        // State
        _runningDataState.value = RunningState()

        // 거리
        lastLocation = null
        totalDistance = 0.0

        // 페이스
        paceInMoment = 0.0
        paceAverage = 0.0
        timeOfLastPaceCalculate = 0L
        distanceAtLastPaceCalculation = 0.0

        // 케이던스
        cadence = 0
        initialSteps = -1L
        currentSteps = 0L
        timeOfLastCadenceCalc = 0L
        stepsAtLastCadenceCalc = 0L
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        startListeners()
    }

    private fun startListeners() {
        startTimer()
        startLocationUpdates()
        startStepCounter()
    }

    private fun stopService() {
        timerJob?.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)

        initStates()
        postCurrentRunningDataState(0L)

        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    // 시간 측정
    private fun startTimer() {
        timerJob = lifecycleScope.launch {
            startTimeInMillis = System.currentTimeMillis()
            while (true) {
                val duration = System.currentTimeMillis() - startTimeInMillis
                postCurrentRunningDataState(duration)
                delay(1000L)
            }
        }
    }

    private fun postCurrentRunningDataState(duration: Long) {
        _runningDataState.value = RunningState(
            duration = duration,
            distance = this.totalDistance,
            paceInMoment = this.paceInMoment,
            paceAverage = this.paceAverage,
            cadence = this.cadence
        )
    }

    // 위치 업데이트 (거리, 페이스 계산)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            updateTotalDistance(result.locations)

            val now = System.currentTimeMillis()

            calculatePaceInMoment(now)
            calculateAvgPace(now)
        }
    }

    private fun updateTotalDistance(locations: List<Location>) {
        locations.forEach { newLocation ->
            lastLocation?.let { lastLocation ->
                val distance = lastLocation.distanceTo(newLocation)
                totalDistance += distance
            }
            lastLocation = newLocation
        }
    }

    private fun calculatePaceInMoment(now: Long) {
        // 5초 이상 차이날때만 계산
        if (now - timeOfLastPaceCalculate >= 5000L) {
            // 이전 계산 이후 이동한 거리와 시간
            val distanceGap = totalDistance - distanceAtLastPaceCalculation
            val timeGap = (now - timeOfLastPaceCalculate) / 1000.0 // 초단위

            // 멈추거나 비정상적인 값 방지
            if (distanceGap < 2f || timeGap < 1.0) {
                this.paceInMoment = 0.0
            } else {
                val speedInMetersPerSecond = distanceGap / timeGap // 속도
                this.paceInMoment = (1000 / speedInMetersPerSecond) / 60 // km 당 분 페이스
            }

            this.timeOfLastPaceCalculate = now
            this.distanceAtLastPaceCalculation = totalDistance
        }
    }

    private fun calculateAvgPace(now: Long) {
        val durationInMillis = now - startTimeInMillis

        // 거리 10 미터 이상, 시간 0 이상
        if (this.totalDistance > 10f && durationInMillis > 0L) {
            val totalDurationInSeconds = durationInMillis / 1000.0
            val avgSpeedInMetersPerSecond = this.totalDistance / totalDurationInSeconds // 속도
            this.paceAverage = (1000 / avgSpeedInMetersPerSecond) / 60 // 평균 페이스
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).apply {
            setMinUpdateIntervalMillis(2000L)
            setMaxUpdateDelayMillis(5000L)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        if (PermissionUtil.hasPermissions(this, SixPackPermissions.LocationPermissions)) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // 3. 걸음 수 측정
    private fun startStepCounter() {
        val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounter?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // 센서 측정값의 정확도가 변경됐을때 - 사용 X
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // sensorManager.registerListener()에 등록한 센서가 새로운 측정값을 생성했을때
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toLong()
            if (this.initialSteps == -1L) {
                this.initialSteps = totalSteps
            }
            this.currentSteps = totalSteps - this.initialSteps

            calculateAvgCadence()
        }
    }

    private fun calculateAvgCadence() {
        val durationInMillis = System.currentTimeMillis() - startTimeInMillis
        if (durationInMillis == 0L || this.currentSteps == 0L) {
            return
        }
        val durationInMinutes = durationInMillis / 1000.0 / 60.0
        this.cadence = (this.currentSteps / durationInMinutes).toInt()
    }

    private fun updateNotification(durationInMillis: Long) {
        val notification = baseNotificationBuilder
            .setContentText(TimeUtil.formatMillisWithDuration(durationInMillis))
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}
