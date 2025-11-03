package com.dpm.sixpack.firebaseMessageService

import com.dpm.sixpack.core.network.di.ApplicationScope
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    @Inject
    @ApplicationScope
    private lateinit var appScope: CoroutineScope

    @Inject
    private lateinit var userPreferenceRepository: UserPreferenceRepository

    /**
     * FCM 메시지를 수신할 때마다 호출됩니다.
     * (앱이 포그라운드에 있을 때 데이터 메시지와 알림 메시지 모두 이곳으로 전달됩니다.)
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 데이터 페이로드 처리 (서버에서 보낸 key-value 데이터)
        if (remoteMessage.data.isNotEmpty()) {
            // 데이터 처리 로직 (예: UI 갱신, 백그라운드 작업)
        }

        // 알림 페이로드 처리 (사용자에게 보일 알림)
        remoteMessage.notification?.let {
            // 알림을 받아 직접 알림(Notification)을 생성하는 로직
            // (앱이 포그라운드일 때만 이곳에서 알림 페이로드를 직접 다룹니다)
        }
    }

    /**
     * FCM 등록 토큰이 갱신될 때마다 호출됩니다. 이 토큰은 기기를 식별하는 고유 ID입니다.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // 이 토큰을 로컬에 저장
        saveTokenToLocal(token)
    }

    private fun saveTokenToLocal(token: String) {
        appScope.launch {
            withContext(Dispatchers.IO) {
                userPreferenceRepository.updateFcmDeviceToken(token)
            }
        }
    }
}
