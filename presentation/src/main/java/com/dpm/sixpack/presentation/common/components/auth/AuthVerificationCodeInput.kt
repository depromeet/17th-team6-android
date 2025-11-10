package com.dpm.sixpack.presentation.common.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.textfield.DoRunSignInputField
import com.dpm.sixpack.presentation.common.util.compose.rememberThrottledClick
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 인증 화면에서 사용되는 인증번호 입력 필드 컴포넌트
 *
 * @param verificationCode 현재 입력된 인증번호
 * @param onVerificationCodeChanged 인증번호 변경 시 실행될 콜백
 * @param topLabel 입력 필드의 라벨 텍스트 (기본값: null, 없으면 라벨 표시 안 함)
 * @param placeholder 플레이스홀더 텍스트
 * @param enabled 입력 필드의 활성화 여부
 * @param showResendButton 재발송 버튼 표시 여부 (기본값: false)
 * @param remainingTime 남은 시간 문자열 (showResendButton이 true일 때만 표시)
 * @param onResendClick 재발송 버튼 클릭 시 실행될 콜백 (showResendButton이 true일 때 필수). 10초 쓰로틀이 자동 적용됩니다.
 */
@Composable
fun AuthVerificationCodeInput(
    verificationCode: String,
    onVerificationCodeChanged: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    showResendButton: Boolean = true,
    remainingTime: String? = null,
    onResendClick: (() -> Unit)? = null,
) {
    val (throttledResendClick, isResendEnabled) =
        rememberThrottledClick(
            throttleTimeMillis = 10_000L,
            onClick = onResendClick ?: {},
        )
    Column(modifier = modifier) {
        DoRunSignInputField(
            value = verificationCode,
            onValueChange = { newValue ->
                // 숫자만 필터링하여 전달 (최대 6자리)
                val digitsOnly = newValue.filter { it.isDigit() }.take(6)
                onVerificationCodeChanged(digitsOnly)
            },
            topLabel = topLabel,
            placeholder = placeholder,
            enabled = enabled,
            keyboardType = KeyboardType.Number,
            singleLine = true,
            trailingIcon = {
                if (showResendButton && remainingTime != null && onResendClick != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = remainingTime,
                            style = SixpackTheme.typography.b2Regular,
                            color = SixpackTheme.colors.red,
                        )
                        DoRunDefaultButton(
                            text = stringResource(R.string.signup_button_resend),
                            onClick = throttledResendClick,
                            enabled = isResendEnabled,
                            textStyle = SixpackTheme.typography.c1Bold,
                            textColor = SixpackTheme.colors.blue600,
                            containerShape = SixpackTheme.shapes.round8,
                            containerColor = SixpackTheme.colors.blue200,
                            disabledTextColor = SixpackTheme.colors.gray500,
                            disabledContainerColor = SixpackTheme.colors.gray200,
                            buttonContentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp),
                            textPadding = PaddingValues(0.dp),
                        )
                    }
                }
            },
        )
    }
}
