package com.dpm.sixpack.firebaseMessageService

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.dpm.sixpack.core.network.di.ApplicationScope
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var userPreferenceRepository: UserPreferenceRepository

    /**
     * FCM 메시지를 수신할 때마다 호출됩니다.
     * (앱이 포그라운드에 있을 때 데이터 메시지와 알림 메시지 모두 이곳으로 전달됩니다.)\
     * 서버가 보낸 '알림(Notification)' 메시지는, 앱이 포그라운드(즉, 사용자가 앱을 켜놓고 보고 있는 상태)일 때
     * 시스템 트레이(상단 바)로 가지 않고 onMessageReceived 함수로 대신 전달
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.d("fcm data received: ${remoteMessage.data}")

        // 데이터 페이로드 처리 (서버에서 보낸 key-value 데이터)
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("fcm data received not null: ${remoteMessage.data}")
        }

        // ⬇️ 포그라운드 상태에서 '알림' 메시지를 받았을 때
        remoteMessage.notification?.let { notification ->
            // ⚠️ 이 로직이 없으면 포그라운드 알림이 무시됩니다.
            Timber.d("Notification received: ${notification.title}, ${notification.body}")
            sendNotification(notification.title, notification.body)
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

        // TODO SK : 서버에 토큰 업데이트 추가
    }

    private fun saveTokenToLocal(token: String) {
        appScope.launch {
            withContext(Dispatchers.IO) {
                userPreferenceRepository.updateFcmDeviceToken(token)
            }
        }
    }

    // 포그라운드 알림을 수동으로 띄우는 함수 예시
    private fun sendNotification(
        title: String?,
        messageBody: String?,
    ) {
        // (알림 채널 생성, PendingIntent 설정 등...)
        // TODO: NotificationManager를 사용하여 알림을 생성하고 띄우는
        //       '알림 빌더(Notification Builder)' 로직 구현

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder =
            NotificationCompat
                .Builder(this, "CHANNEL_ID")
                .setContentTitle(title)
                .setContentText(messageBody)
        notificationManager.notify(0, notificationBuilder.build())
    }
}
