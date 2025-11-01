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
import com.dpm.sixpack.presentation.common.components.auth.AuthPhoneNumberInput
import com.dpm.sixpack.presentation.common.components.auth.AuthScreen
import com.dpm.sixpack.presentation.common.components.auth.AuthVerificationCodeInput
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
        modifier = modifier,
    ) {
        when (state.step) {
            SignInStep.PHONE_INPUT -> {
                AuthPhoneNumberInput(
                    phoneNumber = state.phoneNumber,
                    onPhoneNumberChanged = { onIntent(SignInIntent.OnPhoneNumberChanged(it)) },
                    label = stringResource(R.string.signin_label_phone_number),
                    placeholder = stringResource(R.string.signin_placeholder_phone_number),
                    enabled = !state.isLoading,
                    showClearButton = false,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Find Account Link
                Text(
                    text = stringResource(R.string.signin_find_account),
                    style = SixpackTheme.typography.b2Regular,
                    color = SixpackTheme.colors.gray600,
                )
            }
            SignInStep.VERIFICATION_INPUT -> {
                // Phone Number (Disabled State)
                AuthPhoneNumberInput(
                    phoneNumber = state.phoneNumber,
                    onPhoneNumberChanged = {},
                    label = stringResource(R.string.signin_label_phone_number),
                    placeholder = "",
                    enabled = false,
                    showClearButton = false,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Verification Code Input
                AuthVerificationCodeInput(
                    verificationCode = state.verificationCode,
                    onVerificationCodeChanged = { onIntent(SignInIntent.OnVerificationCodeChanged(it)) },
                    label = stringResource(R.string.signin_label_verification_code),
                    placeholder = stringResource(R.string.signin_placeholder_verification_code),
                    enabled = !state.isLoading && state.remainingTimeInSeconds > 0,
                    showResendButton = false,
                )
            }
        }

        // Error Message
        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                style = SixpackTheme.typography.c1Regular,
                color = SixpackTheme.colors.red,
            )
        }
    }
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
