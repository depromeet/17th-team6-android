package com.dpm.sixpack.runningservice.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.dpm.sixpack.background.R
import com.dpm.sixpack.runningservice.RUNNING_NOTIFICATION_CHANNEL_ID
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
        .Builder(context, RUNNING_NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("달리기 기록을 측정중입니다.")
        .setContentText("00:00:00")
//        .setContentIntent(pendingIntent)
}
