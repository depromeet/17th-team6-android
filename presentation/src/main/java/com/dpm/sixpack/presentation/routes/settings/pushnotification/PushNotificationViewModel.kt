package com.dpm.sixpack.presentation.routes.settings.pushnotification

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.settings.pushnotification.contract.PushNotificationIntent
import com.dpm.sixpack.presentation.routes.settings.pushnotification.contract.PushNotificationSideEffect
import com.dpm.sixpack.presentation.routes.settings.pushnotification.contract.PushNotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PushNotificationViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        // TODO: 푸시 알림 설정 관련 UseCase 주입
    ) : BaseViewModel<PushNotificationState, PushNotificationIntent, PushNotificationSideEffect>() {
        override val initialState: PushNotificationState =
            PushNotificationState(
                // TODO: 실제 사용자의 푸시 알림 설정으로 초기화
                isMarketingPushEnabled = false,
                isNotificationEnabled = true,
                marketingPushConsentDate = "2025.10.27",
            )

        override val container: Container<PushNotificationState, PushNotificationSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            // TODO: 실제 푸시 알림 설정 로드
            // loadPushNotificationSettings()
        }

        override fun onIntent(intent: PushNotificationIntent) {
            when (intent) {
                PushNotificationIntent.OnBackButtonClick -> handleBackButtonClick()
                is PushNotificationIntent.OnMarketingPushToggle -> handleMarketingPushToggle(intent.enabled)
                is PushNotificationIntent.OnNotificationToggle -> handleNotificationToggle(intent.enabled)
            }
        }

        private fun handleBackButtonClick() =
            intent {
                postSideEffect(PushNotificationSideEffect.NavigateBack)
            }

        private fun handleMarketingPushToggle(enabled: Boolean) =
            intent {
                reduce {
                    state.copy(isMarketingPushEnabled = enabled)
                }

                // TODO: 실제 마케팅 푸시 설정 업데이트 API 호출
                if (enabled) {
                    postSideEffect(PushNotificationSideEffect.MarketingPushEnabled)
                } else {
                    postSideEffect(PushNotificationSideEffect.MarketingPushDisabled)
                }
            }

        private fun handleNotificationToggle(enabled: Boolean) =
            intent {
                reduce {
                    state.copy(isNotificationEnabled = enabled)
                }

                // TODO: 실제 알림 설정 업데이트
                if (enabled) {
                    postSideEffect(PushNotificationSideEffect.NotificationEnabled)
                } else {
                    postSideEffect(PushNotificationSideEffect.NotificationDisabled)
                }
            }
    }
