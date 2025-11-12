package com.dpm.sixpack.firebaseMessageService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.dpm.sixpack.background.R
import com.dpm.sixpack.core.network.di.ApplicationScope
import com.dpm.sixpack.domain.usecase.user.SaveFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

const val PRIMARY_PUSH_CHANNEL_ID = "primary_channel"

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var saveFcmTokenUseCase: SaveFcmTokenUseCase

    /**
     * FCM 메시지를 수신할 때마다 호출됩니다.
     * (앱이 포그라운드에 있을 때 데이터 메시지와 알림 메시지 모두 이곳으로 전달됩니다.)\
     * 서버가 보낸 '알림(Notification)' 메시지는, 앱이 포그라운드(즉, 사용자가 앱을 켜놓고 보고 있는 상태)일 때
     * 시스템 트레이(상단 바)로 가지 않고 onMessageReceived 함수로 대신 전달
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.d("fcm data received: ${remoteMessage.data}")

        // '알림'과 '데이터' 둘 다 sendNotification으로 넘깁니다.
        remoteMessage.notification?.let { notification ->
            Timber.d("Notification received: ${notification.title}, ${notification.body}")
            sendNotification(notification, remoteMessage.data)
        }
    }

    /**
     * FCM 등록 토큰이 갱신될 때마다 호출됩니다. 이 토큰은 기기를 식별하는 고유 ID입니다.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Timber.d("Refreshed FCM token: $token")

        // 이 토큰을 로컬에 저장
        saveTokenToLocal(token)
    }

    private fun saveTokenToLocal(token: String) {
        appScope.launch {
            withContext(Dispatchers.IO) {
                saveFcmTokenUseCase(token)
            }
        }
    }

    private fun sendNotification(
        notification: RemoteMessage.Notification, // 1. Notification 객체 전체를 받음
        data: Map<String, String>, // 2. data 페이로드(Map)를 받음
    ) {
        // 3. 딥링크가 있는지 확인
        val deeplink = data["deepLink"]

        // 4. 딥링크 존재 여부에 따라 Intent를 다르게 생성
        val intent: Intent? =
            if (deeplink != null) {
                // ✅ Case 1: 딥링크가 있을 때 (기존 로직)
                Timber.d("Deeplink found: $deeplink")
                Intent(Intent.ACTION_VIEW, deeplink.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            } else {
                // ✅ Case 2: 딥링크가 없을 때 (모듈 분리 솔루션)
                // '패키지 이름'을 기반으로 앱의 '메인 실행 Intent'를 찾습니다.
                // FcmService(Context)는 자신의 packageName을 알고 있습니다.
                Timber.d("No deeplink. Getting launch intent for package: $packageName")
                packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    // 알림을 통해 실행될 때 기존 스택을 정리
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }

        // 5. (안전 장치) Intent 생성에 실패하면 알림을 보내지 않음
        if (intent == null) {
            Timber.e("Could not create intent for notification. (Package: $packageName)")
            return
        }

        // Intent로 PendingIntent 생성
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            )

        val channelId = PRIMARY_PUSH_CHANNEL_ID
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val largeIconBitmap =
            BitmapFactory.decodeResource(
                resources,
                R.drawable.img_logo, // ⬅️ 앱의 메인 아이콘 (컬러)
            )

        val notificationBuilder =
            NotificationCompat
                .Builder(this, channelId)
                .setSmallIcon(R.drawable.img_notification_icon)
                .setLargeIcon(largeIconBitmap)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // 10. 딥링크가 담긴 PendingIntent 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val channel =
            NotificationChannel(
                channelId,
                "기본 알림",
                NotificationManager.IMPORTANCE_HIGH,
            )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, notificationBuilder.build())
    }
}
