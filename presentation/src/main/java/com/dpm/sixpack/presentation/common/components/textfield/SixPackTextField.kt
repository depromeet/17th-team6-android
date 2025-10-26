package com.dpm.sixpack.presentation.common.components.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 공통 TextField 컴포넌트
 * Figma 디자인 가이드에 따라 일관된 스타일 제공
 *
 * @param value 입력값
 * @param onValueChange 입력값 변경 콜백
 * @param label 라벨 텍스트 (선택사항)
 * @param placeholder 플레이스홀더 텍스트 (선택사항)
 * @param enabled 입력 활성화 여부
 * @param isError 에러 상태 여부
 * @param errorMessage 에러 메시지 (선택사항)
 * @param keyboardType 키보드 타입
 * @param trailingIcon 우측 아이콘 (선택사항)
 * @param modifier 레이아웃 모디파이어
 */
@Composable
fun SixPackTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier) {
        // Figma: input field 높이 52px
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            enabled = enabled,
            isError = isError,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = keyboardType,
                ),
            singleLine = singleLine,
            trailingIcon = trailingIcon,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SixpackTheme.colors.gray900,
                    unfocusedTextColor = SixpackTheme.colors.gray900,
                    disabledTextColor = SixpackTheme.colors.gray500,
                    focusedBorderColor = SixpackTheme.colors.blue600,
                    unfocusedBorderColor = SixpackTheme.colors.gray300,
                    disabledBorderColor = SixpackTheme.colors.gray200,
                    errorBorderColor = SixpackTheme.colors.red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = SixpackTheme.colors.blue600,
                    focusedPlaceholderColor = SixpackTheme.colors.gray400,
                    unfocusedPlaceholderColor = SixpackTheme.colors.gray400,
                    focusedLabelColor = SixpackTheme.colors.gray900,
                    unfocusedLabelColor = SixpackTheme.colors.gray500,
                    disabledLabelColor = SixpackTheme.colors.gray500,
                ),
            shape = SixpackTheme.shapes.round12,
        )

        // 에러 메시지 표시 (Figma: 간격 8dp)
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = SixpackTheme.typography.c1Regular,
                color = SixpackTheme.colors.red,
            )
        }
    }
}

/**
 * 비활성화 상태의 완료된 TextField (읽기 전용)
 * Figma 디자인: 52px 높이
 *
 * @param value 입력값
 * @param label 라벨 텍스트 (선택사항)
 * @param modifier 레이아웃 모디파이어
 */
@Composable
fun SixPackTextFieldCompleted(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = label?.let { { Text(it) } },
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp),
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
}

@Preview
@Composable
private fun SixPackTextFieldPreview() {
    DoRunPreviewWrapper {
        Column {
            SixPackTextField(
                value = "",
                onValueChange = {},
                placeholder = "01012345678",
            )

            Spacer(modifier = Modifier.height(24.dp))

            SixPackTextFieldCompleted(
                value = "01012345678",
            )

            Spacer(modifier = Modifier.height(24.dp))

            SixPackTextField(
                value = "",
                onValueChange = {},
                isError = true,
                errorMessage = "올바른 휴대폰 번호를 입력해주세요.",
            )
        }
    }
}
