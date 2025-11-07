package com.dpm.sixpack.presentation.routes.settings.pushnotification.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface PushNotificationIntent : UiIntent {
    data object OnBackButtonClick : PushNotificationIntent

    data class OnMarketingPushToggle(
        val enabled: Boolean,
    ) : PushNotificationIntent

    data class OnNotificationToggle(
        val enabled: Boolean,
    ) : PushNotificationIntent
}
