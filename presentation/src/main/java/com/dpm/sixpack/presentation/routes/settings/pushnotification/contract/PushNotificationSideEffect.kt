package com.dpm.sixpack.presentation.routes.settings.pushnotification.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface PushNotificationSideEffect : SideEffect {
    data object NavigateBack : PushNotificationSideEffect

    data object MarketingPushEnabled : PushNotificationSideEffect

    data object MarketingPushDisabled : PushNotificationSideEffect

    data object NotificationEnabled : PushNotificationSideEffect

    data object NotificationDisabled : PushNotificationSideEffect
}
