package com.dpm.sixpack.presentation.routes.signup.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.auth.AuthScreen
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpIntent
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpState
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpStep

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
                    SignUpStep.PHONE_INPUT -> R.string.signup_title_phone_input
                    SignUpStep.VERIFICATION_INPUT -> R.string.signup_title_verification_input
                },
            ),
        buttonText =
            stringResource(
                when (state.step) {
                    SignUpStep.PHONE_INPUT -> R.string.signup_send_verification_code
                    SignUpStep.VERIFICATION_INPUT -> R.string.common_ok
                },
            ),
        onButtonClick = {
            when (state.step) {
                SignUpStep.PHONE_INPUT -> onIntent(SignUpIntent.OnSendVerificationCodeClick)
                SignUpStep.VERIFICATION_INPUT -> onIntent(SignUpIntent.OnVerifyCodeClick)
            }
        },
        isButtonEnabled = state.isNextButtonEnabled,
        onBackClick = { onIntent(SignUpIntent.OnBackButtonClick) },
        phoneNumber = state.phoneNumber,
        onPhoneNumberChanged = { onIntent(SignUpIntent.OnPhoneNumberChanged(it)) },
        phoneLabel = stringResource(R.string.signup_label_phone_number),
        phonePlaceholder = stringResource(R.string.signup_placeholder_phone_number),
        phoneEnabled = !state.isLoading && state.step == SignUpStep.PHONE_INPUT,
        showPhoneClearButton = true,
        onPhoneClearClick = { onIntent(SignUpIntent.OnPhoneNumberChanged("")) },
        showVerificationInput = state.step == SignUpStep.VERIFICATION_INPUT,
        verificationCode = state.verificationCode,
        onVerificationCodeChanged = { onIntent(SignUpIntent.OnVerificationCodeChanged(it)) },
        verificationPlaceholder = stringResource(R.string.signup_placeholder_verification_code),
        verificationEnabled = !state.isLoading && state.remainingTimeInSeconds > 0,
        showResendButton = true,
        remainingTime = state.formattedRemainingTime,
        onResendClick = { onIntent(SignUpIntent.OnResendCodeClick) },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun SignUpScreenPhoneInputPreview() {
    DoRunPreviewWrapper {
        SignUpScreen(
            state =
                SignUpState(
                    step = SignUpStep.PHONE_INPUT,
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
                    step = SignUpStep.VERIFICATION_INPUT,
                    phoneNumber = "01012345678",
                    verificationCode = "123456",
                    remainingTimeInSeconds = 150,
                ),
            onIntent = {},
        )
    }
}
