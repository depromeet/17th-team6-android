package com.dpm.sixpack.presentation.common.components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.model.PhoneAuthStep
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 인증 관련 화면(회원가입, 로그인)의 공통 레이아웃 컴포넌트
 *
 * @param title 화면 상단에 표시될 제목
 * @param onButtonClick 하단 버튼 클릭 시 실행될 콜백
 * @param isButtonEnabled 하단 버튼의 활성화 여부
 * @param onBackClick 뒤로가기 버튼 클릭 시 실행될 콜백
 * @param phoneNumber 전화번호 입력 값
 * @param onPhoneNumberChanged 전화번호 변경 콜백
 * @param phoneEnabled 전화번호 입력 필드 활성화 여부
 * @param onPhoneClearClick 전화번호 지우기 버튼 클릭 콜백
 * @param verificationCode 인증번호 입력 값
 * @param onVerificationCodeChanged 인증번호 변경 콜백
 * @param verificationEnabled 인증번호 입력 필드 활성화 여부
 * @param remainingTime 남은 시간 문자열
 * @param isLoading 로딩 상태 여부
 * @param onResendClick 재발송 버튼 클릭 콜백 (10초 쓰로틀 자동 적용)
 * @param additionalContentAfterPhone 전화번호 입력 필드 아래에 추가될 컨텐츠 (예: "계정 찾기" 링크)
 */
@Composable
fun AuthScreen(
    title: String,
    step: PhoneAuthStep,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    phoneEnabled: Boolean,
    modifier: Modifier = Modifier,
    verificationCode: String = "",
    onVerificationCodeChanged: (String) -> Unit = {},
    verificationEnabled: Boolean = false,
    remainingTime: String = "",
    isLoading: Boolean = false,
    onPhoneClearClick: (() -> Unit)? = null,
    onResendClick: (() -> Unit)? = null,
    additionalContentAfterPhone: (@Composable () -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = onBackClick,
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
                    text = title,
                    style = SixpackTheme.typography.h2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column {
                    AnimatedVisibility(
                        visible = step == PhoneAuthStep.VERIFICATION_INPUT,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = fadeOut(),
                    ) {
                        AuthVerificationCodeInput(
                            verificationCode = verificationCode,
                            onVerificationCodeChanged = onVerificationCodeChanged,
                            placeholder = stringResource(R.string.signup_placeholder_verification_code),
                            enabled = verificationEnabled,
                            remainingTime = remainingTime,
                            onResendClick = onResendClick,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                    }

                    AuthPhoneNumberInput(
                        phoneNumber = phoneNumber,
                        onPhoneNumberChanged = onPhoneNumberChanged,
                        placeholder = stringResource(R.string.signin_placeholder_phone_number),
                        enabled = phoneEnabled,
                        topLabel =
                            if (step == PhoneAuthStep.PHONE_INPUT) {
                                null
                            } else {
                                stringResource(R.string.signin_label_phone_number)
                            },
                        onClickClear = onPhoneClearClick,
                    )
                }
            }

            Column {
                if (additionalContentAfterPhone != null) {
                    additionalContentAfterPhone()
                }

                // Bottom Button
                DoRunDefaultButton(
                    text =
                        stringResource(
                            when (step) {
                                PhoneAuthStep.PHONE_INPUT -> R.string.signup_send_verification_code
                                PhoneAuthStep.VERIFICATION_INPUT -> R.string.common_ok
                            },
                        ),
                    onClick = onButtonClick,
                    enabled = isButtonEnabled,
                    isLoading = isLoading,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .consumeWindowInsets(paddingValues)
                            .imePadding()
                            .padding(horizontal = SixPackDimen.defaultSideMargin)
                            .padding(top = 8.dp, bottom = 12.dp),
                )
            }
        }
    }
}
