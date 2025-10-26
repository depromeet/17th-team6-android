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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class RunningService : LifecycleService() {
    @Inject
    lateinit var collectAndSaveRunningDataUseCase: CollectAndSaveRunningDataUseCase

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private val binder = RunningBinder()
    private lateinit var notificationManager: NotificationManager

    // 외부 공개 상태 (ViewModel이 관찰)
    private val _runningDataState = MutableStateFlow<RealtimeRunningData?>(null)
    val runningDataState get() = _runningDataState.asStateFlow()

    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        observeUseCaseData()
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
                RunningActions.PAUSE -> pauseService()
                RunningActions.STOP -> stopService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun observeUseCaseData() {
        collectAndSaveRunningDataUseCase.runningDataState
            .onEach { data ->
                _runningDataState.value = data
                data?.let {
                    updateNotification(it.duration)
                }
            }
            .launchIn(lifecycleScope)
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
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        collectAndSaveRunningDataUseCase.start(lifecycleScope)
    }

    private fun resumeService() {
        collectAndSaveRunningDataUseCase.resume(lifecycleScope)
    }

    private fun pauseService() {
        collectAndSaveRunningDataUseCase.pause()
    }

    private fun stopService() {
        isServiceRunning = false
        collectAndSaveRunningDataUseCase.stop()
        _runningDataState.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

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

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceRunning) {
            stopService()
        }
    }
}
