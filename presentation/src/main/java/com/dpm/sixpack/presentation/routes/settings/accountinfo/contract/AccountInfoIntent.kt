package com.dpm.sixpack.presentation.routes.settings.accountinfo.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface AccountInfoIntent : UiIntent {
    data object OnBackButtonClick : AccountInfoIntent
}
