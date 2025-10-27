package com.dpm.sixpack.runningservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.dpm.sixpack.core.util.TimeUtil
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.usecase.CollectAndSaveRunningDataUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RunningService : LifecycleService() {
    @Inject
    lateinit var runningDataUseCase: CollectAndSaveRunningDataUseCase

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private val binder = RunningBinder()

    private lateinit var notificationManager: NotificationManager

    // 외부 공개 상태
    private val _runningDataState = MutableStateFlow<RealtimeRunningData?>(null)
    val runningDataState get() = _runningDataState.asStateFlow()

    // --- 내부 상태 관리 변수 ---
    private var isServiceRunning = false
    private var isPaused = true

    private var dataObserverJob: Job? = null

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
                    if (!isServiceRunning) {
                        isServiceRunning = true
                        startService()
                    } else {
                        resumeService()
                    }
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
        // UseCase의 Scope를 반드시 닫아야함
        runningDataUseCase.close()
        stopService()
    }

    private fun startService() {
        isPaused = false
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        runningDataUseCase.start()
        observeRunningData()
    }

    private fun resumeService() {
        if (isPaused) {
            isPaused = false

            runningDataUseCase.resume()
        }
    }

    private fun pauseService() {
        isPaused = true

        runningDataUseCase.pause()
    }

    private fun stopService() {
        isServiceRunning = false
        isPaused = true

        // UseCase 중지 및 데이터 구독 중지
        dataObserverJob?.cancel()
        dataObserverJob = null
        runningDataUseCase.stop()

        // 서비스의 상태도 초기화
        _runningDataState.value = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // UseCase의 runningDataState를 구독하여
    // Service의 StateFlow와 알림을 업데이트
    private fun observeRunningData() {
        // 이미 실행 중이면 중복 실행 방지
        if (dataObserverJob?.isActive == true) return

        dataObserverJob =
            lifecycleScope.launch {
                runningDataUseCase.runningDataState.collect { data ->
                    // 외부 바인더를 위한 StateFlow 업데이트
                    _runningDataState.value = data

                    // 알림 업데이트 (data가 null이 아닐 때)
                    data?.let {
                        updateNotification(it.duration)
                    }
                }
            }
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
}
