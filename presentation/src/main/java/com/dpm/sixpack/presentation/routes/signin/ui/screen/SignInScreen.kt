package com.dpm.sixpack.presentation.routes.signin.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.auth.AuthScreen
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.signin.contract.SignInIntent
import com.dpm.sixpack.presentation.routes.signin.contract.SignInState
import com.dpm.sixpack.presentation.routes.signin.contract.SignInStep
import com.dpm.sixpack.presentation.theme.SixpackTheme

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
                    SignInStep.PHONE_INPUT -> R.string.signin_title_phone_input
                    SignInStep.VERIFICATION_INPUT -> R.string.signin_title_verification_input
                },
            ),
        buttonText = stringResource(R.string.common_next),
        onButtonClick = {
            when (state.step) {
                SignInStep.PHONE_INPUT -> onIntent(SignInIntent.OnSendVerificationCodeClick)
                SignInStep.VERIFICATION_INPUT -> onIntent(SignInIntent.OnVerifyCodeClick)
            }
        },
        isButtonEnabled = state.isNextButtonEnabled,
        onBackClick = { onIntent(SignInIntent.OnBackButtonClick) },
        phoneNumber = state.phoneNumber,
        onPhoneNumberChanged = { onIntent(SignInIntent.OnPhoneNumberChanged(it)) },
        phoneLabel = stringResource(R.string.signin_label_phone_number),
        phonePlaceholder =
            if (state.step == SignInStep.PHONE_INPUT) {
                stringResource(R.string.signin_placeholder_phone_number)
            } else {
                ""
            },
        phoneEnabled = !state.isLoading && state.step == SignInStep.PHONE_INPUT,
        showVerificationInput = state.step == SignInStep.VERIFICATION_INPUT,
        verificationCode = state.verificationCode,
        onVerificationCodeChanged = { onIntent(SignInIntent.OnVerificationCodeChanged(it)) },
        verificationLabel = stringResource(R.string.signin_label_verification_code),
        verificationPlaceholder = stringResource(R.string.signin_placeholder_verification_code),
        verificationEnabled = !state.isLoading && state.remainingTimeInSeconds > 0,
        errorMessage = state.errorMessage,
        additionalContentAfterPhone =
            if (state.step == SignInStep.PHONE_INPUT) {
                {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.signin_find_account),
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.gray600,
                    )
                }
            } else {
                null
            },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun SignInScreenPhoneInputPreview() {
    DoRunPreviewWrapper {
        SignInScreen(
            state =
                SignInState(
                    step = SignInStep.PHONE_INPUT,
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
                    step = SignInStep.VERIFICATION_INPUT,
                    phoneNumber = "01012345678",
                    verificationCode = "123456",
                    remainingTimeInSeconds = 150,
                ),
            onIntent = {},
        )
    }
}
