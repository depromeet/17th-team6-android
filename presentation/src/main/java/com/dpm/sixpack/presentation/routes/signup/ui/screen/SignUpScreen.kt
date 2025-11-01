package com.dpm.sixpack.presentation.routes.signup.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.textfield.DoRunSignInputField
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.format.PhoneNumberVisualTransformation
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
                Column {
                    AnimatedVisibility(
                        visible = state.step == SignUpStep.VERIFICATION_INPUT,
                        enter = slideInVertically (initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    ) {
                        VerificationCodeInput(
                            verificationCode = state.verificationCode,
                            onVerificationCodeChanged = {
                                onIntent(
                                    SignUpIntent.OnVerificationCodeChanged(it),
                                )
                            },
                            remainingTime = state.formattedRemainingTime,
                            enabled = !state.isLoading && state.remainingTimeInSeconds > 0,
                            onResendClick = {
                                onIntent(SignUpIntent.OnResendCodeClick)
                            },
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }

                    PhoneNumberInput(
                        phoneNumber = state.phoneNumber,
                        onPhoneNumberChanged = { onIntent(SignUpIntent.OnPhoneNumberChanged(it)) },
                        onClickClear = { onIntent(SignUpIntent.OnPhoneNumberChanged("")) },
                        enabled = !state.isLoading && state.step == SignUpStep.PHONE_INPUT,
                    )
                }
            }

            // Bottom Button
            DoRunDefaultButton(
                text =
                    stringResource(
                        when (state.step) {
                            SignUpStep.PHONE_INPUT -> R.string.signup_send_verification_code
                            SignUpStep.VERIFICATION_INPUT -> R.string.common_ok
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
    onClickClear: () -> Unit,
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
        visualTransformation = PhoneNumberVisualTransformation(),
        trailingIcon = {
            if (phoneNumber.isNotBlank()) {
                Image(
                    modifier = Modifier
                        .sizeIn(minWidth = 24.dp, minHeight = 24.dp)
                        .clip(SixpackTheme.shapes.full)
                        .clickable(onClick = onClickClear),
                    painter = painterResource(R.drawable.ic_input_clear),
                    contentDescription = "phone number clear button",
                )
            }
        },
    )
}

@Composable
private fun VerificationCodeInput(
    verificationCode: String,
    onVerificationCodeChanged: (String) -> Unit,
    remainingTime: String,
    enabled: Boolean,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DoRunSignInputField(
            value = verificationCode,
            onValueChange = onVerificationCodeChanged,
            placeholder = stringResource(R.string.signup_placeholder_verification_code),
            enabled = enabled,
            keyboardType = KeyboardType.Number,
            singleLine = true,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = remainingTime,
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.red,
                    )
                    Text(
                        text = stringResource(R.string.signup_button_resend),
                        modifier = Modifier
                            .background(
                                color = SixpackTheme.colors.blue200,
                                shape = SixpackTheme.shapes.round8
                            )
                            .clip(SixpackTheme.shapes.round8)
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .clickable(
                                onClick = onResendClick
                            ),
                        textAlign = TextAlign.Center,
                        style = SixpackTheme.typography.c1Bold,
                        color = SixpackTheme.colors.blue600,
                    )
                }
            },
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
