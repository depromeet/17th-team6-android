package com.dpm.sixpack.presentation.routes.signup.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SignUpIntent : UiIntent {
    data class OnPhoneNumberChanged(
        val phoneNumber: String,
    ) : SignUpIntent

    data class OnVerificationCodeChanged(
        val code: String,
    ) : SignUpIntent

    data object OnSendVerificationCodeClick : SignUpIntent

    data object OnVerifyCodeClick : SignUpIntent

    data object OnResendCodeClick : SignUpIntent

    data object OnBackButtonClick : SignUpIntent
}
