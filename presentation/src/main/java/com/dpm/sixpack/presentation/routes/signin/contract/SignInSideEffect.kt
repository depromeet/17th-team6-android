package com.dpm.sixpack.presentation.routes.signin.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface SignInSideEffect : SideEffect {
    data object NavigateToHome : SignInSideEffect

    data object NavigateBack : SignInSideEffect

    data object ShowInvalidPhoneNumberError : SignInSideEffect

    data object ShowCodeSentSuccess : SignInSideEffect

    data object ShowCodeSendFailedError : SignInSideEffect

    data object ShowInvalidCodeLengthError : SignInSideEffect

    data object ShowCodeMismatchError : SignInSideEffect

    data object ShowCodeExpiredError : SignInSideEffect

    data class ShowUnregisteredUserDialog(
        val phoneNumber: String,
    ) : SignInSideEffect

    data class ShowRegisteredUserDialog(
        val phoneNumber: String,
    ) : SignInSideEffect
}
