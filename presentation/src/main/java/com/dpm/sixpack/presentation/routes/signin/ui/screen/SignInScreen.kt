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
import com.dpm.sixpack.presentation.common.components.dialog.CommonDialog
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
        remainingTime = state.formattedRemainingTime,
        onPhoneClearClick = { onIntent(SignInIntent.OnPhoneNumberChanged("")) },
        onResendClick = { onIntent(SignInIntent.OnResendCodeClick) },
        additionalContentAfterPhone =
            if (state.step == PhoneAuthStep.PHONE_INPUT) {
                {
                    AuthClickableTextLink(
                        normalText = stringResource(R.string.signin_find_account_question),
                        linkText = stringResource(R.string.signin_find_account_action),
                        onLinkClick = { onIntent(SignInIntent.OnFindAccountClick) },
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
            } else {
                null
            },
    )

    if (state.showUnregisteredDialog) {
        CommonDialog(
            title = stringResource(R.string.signin_unregistered_user_title),
            description = stringResource(R.string.signin_unregistered_user_message),
            onDismiss = {
                onIntent(SignInIntent.OnDismissUnregisteredDialog)
            },
            primaryButtonText = stringResource(R.string.singin_signup_action),
            primaryButtonOnClick = {
                onIntent(SignInIntent.OnSignUpClick(state.phoneNumber))
            },
            secondaryButtonText = stringResource(R.string.common_cancel),
            secondaryButtonOnClick = {
                onIntent(SignInIntent.OnDismissUnregisteredDialog)
            },
        )
    }
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
