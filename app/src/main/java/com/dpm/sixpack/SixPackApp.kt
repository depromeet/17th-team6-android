package com.dpm.sixpack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.dpm.sixpack.fcm.PRIMARY_PUSH_CHANNEL_ID
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
        createNotificationChannel()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun createNotificationChannel() {
        val name = "푸시 알림"
        val descriptionText = "앱의 주요 알림을 받습니다."
        val importance = NotificationManager.IMPORTANCE_HIGH

        // 3. 채널 객체 생성
        val channel =
            NotificationChannel(PRIMARY_PUSH_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

        // 4. NotificationManager를 통해 시스템에 이 채널을 등록
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()
}
