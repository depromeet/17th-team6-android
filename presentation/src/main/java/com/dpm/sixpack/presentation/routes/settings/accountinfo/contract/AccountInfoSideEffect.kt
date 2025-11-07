package com.dpm.sixpack.presentation.routes.settings.accountinfo.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface AccountInfoSideEffect : SideEffect {
    data object NavigateBack : AccountInfoSideEffect
}
