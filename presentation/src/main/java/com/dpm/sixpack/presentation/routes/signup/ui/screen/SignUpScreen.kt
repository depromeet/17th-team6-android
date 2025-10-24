package com.dpm.sixpack.presentation.routes.signup.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
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
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = SixPackDimen.defaultSideMargin),
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text =
                        when (state.step) {
                            SignUpStep.PHONE_INPUT -> "환영합니다!\n휴대폰 번호로 가입해주세요."
                            SignUpStep.VERIFICATION_INPUT -> "인증번호 6자리를\n입력해주세요."
                        },
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
                        // Phone Number (Completed State)
                        OutlinedTextField(
                            value = state.phoneNumber,
                            onValueChange = {},
                            label = { Text("휴대폰 번호") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = SixpackTheme.colors.gray900,
                                    disabledBorderColor = SixpackTheme.colors.gray300,
                                    disabledLabelColor = SixpackTheme.colors.gray500,
                                    focusedBorderColor = SixpackTheme.colors.blue600,
                                    unfocusedBorderColor = SixpackTheme.colors.gray300,
                                    disabledContainerColor = Color.Transparent,
                                ),
                            shape = SixpackTheme.shapes.round12,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Verification Code Input
                        VerificationCodeInput(
                            verificationCode = state.verificationCode,
                            onVerificationCodeChanged = { onIntent(SignUpIntent.OnVerificationCodeChanged(it)) },
                            remainingTime = state.formattedRemainingTime,
                            enabled = !state.isLoading && state.remainingTimeInSeconds > 0,
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

            // Bottom Button
            DoRunDefaultButton(
                text =
                    when (state.step) {
                        SignUpStep.PHONE_INPUT -> "다음"
                        SignUpStep.VERIFICATION_INPUT -> "완료"
                    },
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
                        .padding(horizontal = SixPackDimen.defaultSideMargin)
                        .padding(bottom = 12.dp)
                        .align(Alignment.BottomCenter),
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
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChanged,
        placeholder = { Text("010-0000-0000") },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
        singleLine = true,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = SixpackTheme.colors.gray900,
                unfocusedTextColor = SixpackTheme.colors.gray900,
                disabledTextColor = SixpackTheme.colors.gray500,
                focusedBorderColor = SixpackTheme.colors.blue600,
                unfocusedBorderColor = SixpackTheme.colors.gray300,
                disabledBorderColor = SixpackTheme.colors.gray200,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = SixpackTheme.colors.blue600,
                focusedPlaceholderColor = SixpackTheme.colors.gray400,
                unfocusedPlaceholderColor = SixpackTheme.colors.gray400,
            ),
        shape = SixpackTheme.shapes.round12,
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
    OutlinedTextField(
        value = verificationCode,
        onValueChange = onVerificationCodeChanged,
        placeholder = { Text("000000") },
        trailingIcon = {
            Text(
                text = remainingTime,
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.red,
            )
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
        singleLine = true,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = SixpackTheme.colors.gray900,
                unfocusedTextColor = SixpackTheme.colors.gray900,
                disabledTextColor = SixpackTheme.colors.gray500,
                focusedBorderColor = SixpackTheme.colors.blue600,
                unfocusedBorderColor = SixpackTheme.colors.gray300,
                disabledBorderColor = SixpackTheme.colors.gray200,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = SixpackTheme.colors.blue600,
                focusedPlaceholderColor = SixpackTheme.colors.gray400,
                unfocusedPlaceholderColor = SixpackTheme.colors.gray400,
            ),
        shape = SixpackTheme.shapes.round12,
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
