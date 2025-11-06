package com.dpm.sixpack.presentation.routes.settings.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface SettingsSideEffect : SideEffect {
    data object NavigateBack : SettingsSideEffect

    data object NavigateToProfileEdit : SettingsSideEffect

    data object NavigateToAccountInfo : SettingsSideEffect

    data object NavigateToPushNotification : SettingsSideEffect

    data class NavigateToExternalUrl(
        val url: String,
    ) : SettingsSideEffect

    data object LogoutSuccess : SettingsSideEffect

    data object LogoutFailed : SettingsSideEffect

    data object WithdrawSuccess : SettingsSideEffect

    data object WithdrawFailed : SettingsSideEffect
}
