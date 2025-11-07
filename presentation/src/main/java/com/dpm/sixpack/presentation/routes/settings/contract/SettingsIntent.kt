package com.dpm.sixpack.presentation.routes.settings.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SettingsIntent : UiIntent {
    data object OnBackButtonClick : SettingsIntent

    data object OnProfileEditClick : SettingsIntent

    data object OnAccountInfoClick : SettingsIntent

    data object OnPushNotificationClick : SettingsIntent

    data object OnPrivacyPolicyClick : SettingsIntent

    data object OnTermsClick : SettingsIntent

    data object OnLogoutClick : SettingsIntent

    data object OnWithdrawClick : SettingsIntent

    data object OnLogoutConfirm : SettingsIntent

    data object OnWithdrawConfirm : SettingsIntent

    data object OnDismissLogoutDialog : SettingsIntent

    data object OnDismissWithdrawDialog : SettingsIntent
}
