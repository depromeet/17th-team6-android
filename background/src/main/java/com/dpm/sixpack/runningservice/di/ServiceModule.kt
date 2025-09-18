package com.dpm.sixpack.runningservice.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.dpm.sixpack.runningservice.NOTIFICATION_CHANNEL_ID
import com.dpm.sixpack.runningservice.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
//        pendingIntent: PendingIntent
    ) = NotificationCompat
        .Builder(context, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.run_small_icon)
        .setContentTitle("달리기 기록을 측정중입니다.")
        .setContentText("00:00:00")
//        .setContentIntent(pendingIntent)
}
