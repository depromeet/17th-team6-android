package com.dpm.sixpack.firebaseMessageService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
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
        // 3. ⚠️ data에서 deeplink 값을 추출합니다.
        //    (로그를 보니 "deeplink" 키를 사용하고 계십니다)
        val deeplinkUri =
            data["deeplink"]?.let {
                Uri.parse(it)
            } ?: Uri.parse("dorundorun://main") // 딥링크가 없을 경우의 기본 Uri (필요시 수정)

        // 딥링크 Uri로 Intent 생성
        val intent =
            Intent(Intent.ACTION_VIEW, deeplinkUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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

        val notificationBuilder =
            NotificationCompat
                .Builder(this, channelId)
                .setSmallIcon(R.drawable.ill_loading_overlay)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // 10. 딥링크가 담긴 PendingIntent 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        // 11. (필수) Android 8.0 이상에서 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "기본 알림",
                    NotificationManager.IMPORTANCE_HIGH,
                )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
