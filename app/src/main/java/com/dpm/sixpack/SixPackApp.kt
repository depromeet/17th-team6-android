package com.dpm.sixpack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.dpm.sixpack.fcm.NotificationChannelIds
import com.dpm.sixpack.presentation.common.util.navermap.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SixPackApp :
    Application(),
    SingletonImageLoader.Factory {
    @Inject
    lateinit var initializers: AppInitializer

    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    override fun onCreate() {
        super.onCreate()

        initializers.onAppCreate(this)

        setDarkMode()
        initTimber()
        createNotificationChannels()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        // 1. 응원 채널 (중요도 HIGH: 팝업 + 소리)
        val cheerChannel =
            NotificationChannel(
                NotificationChannelIds.CHEER,
                "응원 및 깨우기",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "친구가 보낸 응원 및 깨우기 알림입니다."
            }

        // 2. 소셜 채널 (중요도 DEFAULT: 소리)
        val socialChannel =
            NotificationChannel(
                NotificationChannelIds.SOCIAL,
                "피드 및 소셜 활동",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "새 피드, 좋아요 등 친구들의 활동 소식입니다."
            }

        // 3. 리마인더 채널 (중요도 LOW: 조용히)
        val reminderChannel =
            NotificationChannel(
                NotificationChannelIds.REMINDER,
                "활동 알림",
                NotificationManager.IMPORTANCE_LOW, // (소리 X, 팝업 X, 트레이에만 표시)
            ).apply {
                description = "러닝 및 피드 업로드를 독려하는 알림입니다."
            }

        // 시스템에 모든 채널 등록
        manager.createNotificationChannel(cheerChannel)
        manager.createNotificationChannel(socialChannel)
        manager.createNotificationChannel(reminderChannel)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()
}
