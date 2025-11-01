package com.dpm.sixpack.presentation.routes.signup.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.auth.AuthPhoneNumberInput
import com.dpm.sixpack.presentation.common.components.auth.AuthScreen
import com.dpm.sixpack.presentation.common.components.auth.AuthVerificationCodeInput
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
        modifier = modifier,
    ) {
        Column {
            AnimatedVisibility(
                visible = state.step == SignUpStep.VERIFICATION_INPUT,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                AuthVerificationCodeInput(
                    verificationCode = state.verificationCode,
                    onVerificationCodeChanged = {
                        onIntent(SignUpIntent.OnVerificationCodeChanged(it))
                    },
                    placeholder = stringResource(R.string.signup_placeholder_verification_code),
                    enabled = !state.isLoading && state.remainingTimeInSeconds > 0,
                    showResendButton = true,
                    remainingTime = state.formattedRemainingTime,
                    onResendClick = {
                        onIntent(SignUpIntent.OnResendCodeClick)
                    },
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }

            AuthPhoneNumberInput(
                phoneNumber = state.phoneNumber,
                onPhoneNumberChanged = { onIntent(SignUpIntent.OnPhoneNumberChanged(it)) },
                label = stringResource(R.string.signup_label_phone_number),
                placeholder = stringResource(R.string.signup_placeholder_phone_number),
                enabled = !state.isLoading && state.step == SignUpStep.PHONE_INPUT,
                showClearButton = true,
                onClickClear = { onIntent(SignUpIntent.OnPhoneNumberChanged("")) },
            )
        }
    }
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
