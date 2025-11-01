package com.dpm.sixpack.presentation.routes.signin.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.auth.AuthClickableTextLink
import com.dpm.sixpack.presentation.common.components.auth.AuthScreen
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.PhoneAuthStep
import com.dpm.sixpack.presentation.routes.signin.contract.SignInIntent
import com.dpm.sixpack.presentation.routes.signin.contract.SignInState

@Composable
fun SignInScreen(
    state: SignInState,
    onIntent: (SignInIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AuthScreen(
        title =
            stringResource(
                when (state.step) {
                    PhoneAuthStep.PHONE_INPUT -> R.string.signin_title_phone_input
                    PhoneAuthStep.VERIFICATION_INPUT -> R.string.signin_title_verification_input
                },
            ),
        step = state.step,
        onButtonClick = {
            when (state.step) {
                PhoneAuthStep.PHONE_INPUT -> onIntent(SignInIntent.OnSendVerificationCodeClick)
                PhoneAuthStep.VERIFICATION_INPUT -> onIntent(SignInIntent.OnVerifyCodeClick)
            }
        },
        isButtonEnabled = state.isNextButtonEnabled,
        onBackClick = { onIntent(SignInIntent.OnBackButtonClick) },
        phoneNumber = state.phoneNumber,
        onPhoneNumberChanged = { onIntent(SignInIntent.OnPhoneNumberChanged(it)) },
        phoneEnabled = !state.isLoading && state.step == PhoneAuthStep.PHONE_INPUT,
        modifier = modifier,
        verificationCode = state.verificationCode,
        onVerificationCodeChanged = { onIntent(SignInIntent.OnVerificationCodeChanged(it)) },
        verificationEnabled = !state.isLoading && state.remainingTimeInSeconds > 0,
        additionalContentAfterPhone =
            if (state.step == PhoneAuthStep.PHONE_INPUT) {
                {
                    AuthClickableTextLink(
                        normalText = "",
                        linkText = stringResource(R.string.signin_find_account),
                        onLinkClick = { /* TODO: 계정 찾기 기능 구현 */ },
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
            } else {
                null
            },
    )
}

@Preview
@Composable
private fun SignInScreenPhoneInputPreview() {
    DoRunPreviewWrapper {
        SignInScreen(
            state =
                SignInState(
                    step = PhoneAuthStep.PHONE_INPUT,
                    phoneNumber = "01012345678",
                ),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun SignInScreenVerificationInputPreview() {
    DoRunPreviewWrapper {
        SignInScreen(
            state =
                SignInState(
                    step = PhoneAuthStep.VERIFICATION_INPUT,
                    phoneNumber = "01012345678",
                    verificationCode = "123456",
                    remainingTimeInSeconds = 150,
                ),
            onIntent = {},
        )
    }
}
