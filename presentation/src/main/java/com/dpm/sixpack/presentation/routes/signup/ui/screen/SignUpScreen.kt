package com.dpm.sixpack.presentation.routes.signup.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.auth.AuthScreen
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.PhoneAuthStep
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpIntent
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpState

@Composable
fun SignUpScreen(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AuthScreen(
        title =
            stringResource(
                when (state.step) {
                    PhoneAuthStep.PHONE_INPUT -> R.string.signup_title_phone_input
                    PhoneAuthStep.VERIFICATION_INPUT -> R.string.signup_title_verification_input
                },
            ),
        step = state.step,
        onButtonClick = {
            when (state.step) {
                PhoneAuthStep.PHONE_INPUT -> onIntent(SignUpIntent.OnSendVerificationCodeClick)
                PhoneAuthStep.VERIFICATION_INPUT -> onIntent(SignUpIntent.OnVerifyCodeClick)
            }
        },
        isButtonEnabled = state.isNextButtonEnabled,
        onBackClick = { onIntent(SignUpIntent.OnBackButtonClick) },
        phoneNumber = state.phoneNumber,
        onPhoneNumberChanged = { onIntent(SignUpIntent.OnPhoneNumberChanged(it)) },
        phoneEnabled = !state.isLoading && state.step == PhoneAuthStep.PHONE_INPUT,
        modifier = modifier,
        verificationCode = state.verificationCode,
        onVerificationCodeChanged = { onIntent(SignUpIntent.OnVerificationCodeChanged(it)) },
        verificationEnabled = !state.isLoading && state.remainingTimeInSeconds > 0,
        remainingTime = state.formattedRemainingTime,
        onPhoneClearClick = { onIntent(SignUpIntent.OnPhoneNumberChanged("")) },
        onResendClick = { onIntent(SignUpIntent.OnResendCodeClick) },
    )
}

@Preview
@Composable
private fun SignUpScreenPhoneInputPreview() {
    DoRunPreviewWrapper {
        SignUpScreen(
            state =
                SignUpState(
                    step = PhoneAuthStep.PHONE_INPUT,
                    phoneNumber = "01012345678",
                ),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun SignUpScreenVerificationInputPreview() {
    DoRunPreviewWrapper {
        SignUpScreen(
            state =
                SignUpState(
                    step = PhoneAuthStep.VERIFICATION_INPUT,
                    phoneNumber = "01012345678",
                    verificationCode = "123456",
                    remainingTimeInSeconds = 150,
                ),
            onIntent = {},
        )
    }
}
