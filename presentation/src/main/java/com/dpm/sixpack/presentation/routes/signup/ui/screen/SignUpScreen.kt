package com.dpm.sixpack.presentation.routes.signup.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.textfield.DoRunSignInputField
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpIntent
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpState
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpStep
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun SignUpScreen(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(SignUpIntent.OnBackButtonClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = SixPackDimen.defaultSideMargin),
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text =
                        stringResource(
                            when (state.step) {
                                SignUpStep.PHONE_INPUT -> R.string.signup_title_phone_input
                                SignUpStep.VERIFICATION_INPUT -> R.string.signup_title_verification_input
                            },
                        ),
                    style = SixpackTheme.typography.h2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input Fields
                when (state.step) {
                    SignUpStep.PHONE_INPUT -> {
                        PhoneNumberInput(
                            phoneNumber = state.phoneNumber,
                            onPhoneNumberChanged = { onIntent(SignUpIntent.OnPhoneNumberChanged(it)) },
                            enabled = !state.isLoading,
                        )
                    }
                    SignUpStep.VERIFICATION_INPUT -> {
                        // Verification Code Input
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                        ) {
                            Column {
                                VerificationCodeInput(
                                    verificationCode = state.verificationCode,
                                    onVerificationCodeChanged = { onIntent(SignUpIntent.OnVerificationCodeChanged(it)) },
                                    remainingTime = state.formattedRemainingTime,
                                    enabled = !state.isLoading && state.remainingTimeInSeconds > 0,
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Phone Number (Disabled State)
                                DoRunSignInputField(
                                    value = state.phoneNumber,
                                    onValueChange = {},
                                    label = stringResource(R.string.signup_label_phone_number),
                                    enabled = false,
                                )
                            }
                        }
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

            // Bottom Button
            DoRunDefaultButton(
                text =
                    stringResource(
                        when (state.step) {
                            SignUpStep.PHONE_INPUT -> R.string.common_next
                            SignUpStep.VERIFICATION_INPUT -> R.string.common_next
                        },
                    ),
                onClick = {
                    when (state.step) {
                        SignUpStep.PHONE_INPUT -> onIntent(SignUpIntent.OnSendVerificationCodeClick)
                        SignUpStep.VERIFICATION_INPUT -> onIntent(SignUpIntent.OnVerifyCodeClick)
                    }
                },
                enabled = state.isNextButtonEnabled,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
                        .padding(horizontal = SixPackDimen.defaultSideMargin)
                        .padding(bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun PhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    DoRunSignInputField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChanged,
        label = stringResource(R.string.signup_label_phone_number),
        placeholder = stringResource(R.string.signup_placeholder_phone_number),
        modifier = modifier,
        enabled = enabled,
        keyboardType = KeyboardType.Number,
        singleLine = true,
    )
}

@Composable
private fun VerificationCodeInput(
    verificationCode: String,
    onVerificationCodeChanged: (String) -> Unit,
    remainingTime: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DoRunSignInputField(
            value = verificationCode,
            onValueChange = onVerificationCodeChanged,
            label = stringResource(R.string.signup_label_verification_code),
            placeholder = stringResource(R.string.signup_placeholder_verification_code),
            enabled = enabled,
            keyboardType = KeyboardType.Number,
            singleLine = true,
        )
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
