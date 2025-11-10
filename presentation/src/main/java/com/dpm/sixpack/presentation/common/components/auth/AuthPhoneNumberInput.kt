package com.dpm.sixpack.presentation.common.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.textfield.DoRunSignInputField
import com.dpm.sixpack.presentation.common.util.format.PhoneNumberVisualTransformation
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 인증 화면에서 사용되는 전화번호 입력 필드 컴포넌트
 *
 * @param phoneNumber 현재 입력된 전화번호
 * @param onPhoneNumberChanged 전화번호 변경 시 실행될 콜백
 * @param label 입력 필드의 라벨 텍스트
 * @param placeholder 플레이스홀더 텍스트
 * @param enabled 입력 필드의 활성화 여부
 * @param onClickClear 지우기 버튼 클릭 시 실행될 콜백 (기본값: null, showClearButton이 true일 때 필수)
 */
@Composable
fun AuthPhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    onClickClear: (() -> Unit)? = null,
) {
    DoRunSignInputField(
        value = phoneNumber,
        onValueChange = { newValue ->
            // 숫자만 필터링하여 전달 (최대 11자리)
            val digitsOnly = newValue.filter { it.isDigit() }.take(11)
            onPhoneNumberChanged(digitsOnly)
        },
        topLabel = topLabel,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled,
        keyboardType = KeyboardType.Number,
        singleLine = true,
        visualTransformation = PhoneNumberVisualTransformation(),
        trailingIcon = {
            if (phoneNumber.isNotBlank() && onClickClear != null && enabled) {
                Image(
                    modifier =
                        Modifier
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
