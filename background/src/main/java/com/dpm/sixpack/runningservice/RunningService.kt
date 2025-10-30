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
import com.dpm.sixpack.domain.usecase.SyncRunningDataUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RunningService : LifecycleService() {
    @Inject
    lateinit var runningDataUseCase: CollectAndSaveRunningDataUseCase

    @Inject
    lateinit var syncRunningDataUseCase: SyncRunningDataUseCase

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
    private var syncTimerJob: Job? = null

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

        // 1. 실시간 데이터 수집 및 UI 발행 시작
        runningDataUseCase.start()
        observeRunningData()

        // 2. 5분 주기 동기화 타이머 시작
        startSyncTimer()
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

        // 5분 동기화 타이머 중지
        syncTimerJob?.cancel()
        syncTimerJob = null

        // 서비스의 상태도 초기화
        _runningDataState.value = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // UseCase의 runningDataState를 구독하여 Service의 StateFlow와 알림을 업데이트
    private fun observeRunningData() {
        if (dataObserverJob?.isActive == true) return

        dataObserverJob =
            lifecycleScope.launch {
                runningDataUseCase.runningDataState.collect { data ->
                    _runningDataState.value = data

                    // 알림 업데이트 (data가 null이 아닐 때)
                    data?.let {
                        updateNotification(it.durationInSec)
                    }
                }
            }
    }

    /**
     * 5분마다 세그먼트 데이터를 동기화하는 타이머를 시작
     */
    private fun startSyncTimer() {
        if (syncTimerJob?.isActive == true) return // 이미 실행 중이면 무시

        syncTimerJob =
            lifecycleScope.launch {
                while (isActive) {
                    // 1. 5분 대기
                    delay(SYNC_INTERVAL_MS)

                    // 서비스가 실행 중일 때만 (일시정지 여부와 관계없이) 동기화
                    if (isServiceRunning) {
                        Timber.d("5-minute sync timer. Attempting to sync segments...")
                        // 3. 동기화 UseCase 호출
                        when (val result = syncRunningDataUseCase(isPaused)) {
                            is DoRunResult.Success -> {
                                Timber.d("5-minute sync successful.")
                            }

                            is DoRunResult.Failure -> {
                                Timber.w(result.exception, "5-minute sync failed.")
                            }
                        }
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

    companion object {
        private const val SYNC_INTERVAL_MS = 5 * 60 * 1000L
    }
}
