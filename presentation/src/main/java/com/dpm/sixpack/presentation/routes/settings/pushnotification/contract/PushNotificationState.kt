package com.dpm.sixpack.presentation.routes.settings.pushnotification.contract

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class PushNotificationState(
    val isMarketingPushEnabled: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val marketingPushConsentDate: String? = null,
) : UiState
