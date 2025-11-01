package com.dpm.sixpack.presentation.common.components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 인증 관련 화면(회원가입, 로그인)의 공통 레이아웃 컴포넌트
 *
 * @param title 화면 상단에 표시될 제목
 * @param buttonText 하단 버튼에 표시될 텍스트
 * @param onButtonClick 하단 버튼 클릭 시 실행될 콜백
 * @param isButtonEnabled 하단 버튼의 활성화 여부
 * @param onBackClick 뒤로가기 버튼 클릭 시 실행될 콜백
 * @param phoneNumber 전화번호 입력 값
 * @param onPhoneNumberChanged 전화번호 변경 콜백
 * @param phoneLabel 전화번호 입력 필드 라벨
 * @param phonePlaceholder 전화번호 입력 필드 플레이스홀더
 * @param phoneEnabled 전화번호 입력 필드 활성화 여부
 * @param showPhoneClearButton 전화번호 지우기 버튼 표시 여부
 * @param onPhoneClearClick 전화번호 지우기 버튼 클릭 콜백
 * @param showVerificationInput 인증번호 입력 필드 표시 여부 (애니메이션 적용)
 * @param verificationCode 인증번호 입력 값
 * @param onVerificationCodeChanged 인증번호 변경 콜백
 * @param verificationLabel 인증번호 입력 필드 라벨
 * @param verificationPlaceholder 인증번호 입력 필드 플레이스홀더
 * @param verificationEnabled 인증번호 입력 필드 활성화 여부
 * @param showResendButton 재발송 버튼 표시 여부
 * @param remainingTime 남은 시간 문자열
 * @param onResendClick 재발송 버튼 클릭 콜백
 * @param errorMessage 에러 메시지
 * @param additionalContentAfterPhone 전화번호 입력 필드 아래에 추가될 컨텐츠 (예: "계정 찾기" 링크)
 */
@Composable
fun AuthScreen(
    title: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    phoneLabel: String,
    phonePlaceholder: String,
    phoneEnabled: Boolean,
    modifier: Modifier = Modifier,
    showPhoneClearButton: Boolean = false,
    onPhoneClearClick: (() -> Unit)? = null,
    showVerificationInput: Boolean = false,
    verificationCode: String = "",
    onVerificationCodeChanged: (String) -> Unit = {},
    verificationLabel: String? = null,
    verificationPlaceholder: String = "",
    verificationEnabled: Boolean = false,
    showResendButton: Boolean = false,
    remainingTime: String? = null,
    onResendClick: (() -> Unit)? = null,
    errorMessage: String? = null,
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

                // Content - SignUp 기준 구조: VerificationInput (위) + PhoneInput (아래)
                Column {
                    // Verification Code Input (AnimatedVisibility)
                    AnimatedVisibility(
                        visible = showVerificationInput,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    ) {
                        AuthVerificationCodeInput(
                            verificationCode = verificationCode,
                            onVerificationCodeChanged = onVerificationCodeChanged,
                            label = verificationLabel,
                            placeholder = verificationPlaceholder,
                            enabled = verificationEnabled,
                            showResendButton = showResendButton,
                            remainingTime = remainingTime,
                            onResendClick = onResendClick,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                    }

                    // Phone Number Input
                    AuthPhoneNumberInput(
                        phoneNumber = phoneNumber,
                        onPhoneNumberChanged = onPhoneNumberChanged,
                        label = phoneLabel,
                        placeholder = phonePlaceholder,
                        enabled = phoneEnabled,
                        showClearButton = showPhoneClearButton,
                        onClickClear = onPhoneClearClick,
                    )

                    // Additional Content After Phone (예: "계정 찾기" 링크)
                    additionalContentAfterPhone?.invoke()

                    // Error Message
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            style = SixpackTheme.typography.c1Regular,
                            color = SixpackTheme.colors.red,
                        )
                    }
                }
            }

            // Bottom Button
            DoRunDefaultButton(
                text = buttonText,
                onClick = onButtonClick,
                enabled = isButtonEnabled,
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
