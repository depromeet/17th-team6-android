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
 * 공통 입력필드 컴포넌트 (Figma: input field)
 * 여러 스타일과 상태를 지원합니다.
 *
 * @param value 입력값
 * @param onValueChange 입력값 변경 콜백
 * @param label 라벨 텍스트
 * @param placeholder 플레이스홀더 텍스트 (선택사항)
 * @param enabled 입력 활성화 여부
 * @param isError 에러 상태 여부
 * @param style "default" 또는 "id" 스타일
 * @param keyboardType 키보드 타입
 * @param modifier 레이아웃 모디파이어
 */
@Composable
fun DoRunSignInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    style: String = "default",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        Text(
            text = label,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
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
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SixpackTheme.colors.gray900,
                    unfocusedTextColor = SixpackTheme.colors.gray900,
                    disabledTextColor = SixpackTheme.colors.gray500,
                    focusedBorderColor = SixpackTheme.colors.gray900,
                    unfocusedBorderColor = SixpackTheme.colors.gray200,
                    disabledBorderColor = SixpackTheme.colors.gray200,
                    errorBorderColor = SixpackTheme.colors.red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = SixpackTheme.colors.blue600,
                    focusedPlaceholderColor = SixpackTheme.colors.gray400,
                    unfocusedPlaceholderColor = SixpackTheme.colors.gray400,
                ),
            shape = SixpackTheme.shapes.round8,
        )
    }
}

@Preview
@Composable
private fun CommonInputFieldPreview() {
    DoRunPreviewWrapper {
        Column(modifier = Modifier.fillMaxWidth()) {
            DoRunSignInputField(
                value = "",
                onValueChange = {},
                label = "라벨 텍스트",
                placeholder = "플레이스홀더",
            )

            Spacer(modifier = Modifier.height(24.dp))

            DoRunSignInputField(
                value = "입력된 텍스트",
                onValueChange = {},
                label = "라벨 텍스트",
            )

            Spacer(modifier = Modifier.height(24.dp))

            DoRunSignInputField(
                value = "",
                onValueChange = {},
                label = "라벨 텍스트",
                isError = true,
            )
        }
    }
}
