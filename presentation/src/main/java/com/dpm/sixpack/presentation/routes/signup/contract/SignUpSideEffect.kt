package com.dpm.sixpack.presentation.routes.signup.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface SignUpSideEffect : SideEffect {
    data object NavigateToProfileCreation : SignUpSideEffect

    data object NavigateBack : SignUpSideEffect

    data object NavigateToFindAccount : SignUpSideEffect

    data object ShowInvalidPhoneNumberError : SignUpSideEffect

    data object ShowCodeSentSuccess : SignUpSideEffect

    data object ShowCodeSendFailedError : SignUpSideEffect

    data object ShowRateLimitError : SignUpSideEffect

    data object ShowInvalidCodeLengthError : SignUpSideEffect

    data object ShowCodeMismatchError : SignUpSideEffect

    data object ShowCodeExpiredError : SignUpSideEffect
}
