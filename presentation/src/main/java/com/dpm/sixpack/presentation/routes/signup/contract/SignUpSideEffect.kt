package com.dpm.sixpack.presentation.routes.signup.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface SignUpSideEffect : SideEffect {
    data object NavigateToTermsAgreement : SignUpSideEffect

    data object NavigateBack : SignUpSideEffect

    data class ShowToast(
        val message: String,
    ) : SignUpSideEffect
}
