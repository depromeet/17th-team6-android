package com.dpm.sixpack.presentation.routes.signin.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SignInIntent : UiIntent {
    data class OnPhoneNumberChanged(
        val phoneNumber: String,
    ) : SignInIntent

    data class OnVerificationCodeChanged(
        val code: String,
    ) : SignInIntent

    data object OnSendVerificationCodeClick : SignInIntent

    data object OnVerifyCodeClick : SignInIntent

    data object OnResendCodeClick : SignInIntent

    data object OnBackButtonClick : SignInIntent

    data object OnDismissUnregisteredDialog : SignInIntent

    data class OnSignUpClick(
        val phoneNumber: String
    ) : SignInIntent
}
