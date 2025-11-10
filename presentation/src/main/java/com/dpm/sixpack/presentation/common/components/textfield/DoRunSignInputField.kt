package com.dpm.sixpack.presentation.common.components.textfield

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
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
 * @param topLabel 라벨 텍스트
 * @param placeholder 플레이스홀더 텍스트 (선택사항)
 * @param enabled 입력 활성화 여부
 * @param isError 에러 상태 여부
 * @param style "default" 또는 "id" 스타일
 * @param keyboardType 키보드 타입
 * @param maxLength 최대 입력 길이 제한 (선택사항)
 * @param trailingIcon 우측 아이콘 컨텐츠 (선택사항)
 * @param bottomHelper 보조 텍스트 (선택사항)
 * @param modifier 레이아웃 모디파이어
 */
@Composable
fun DoRunSignInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    topLabel: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    style: String = "default",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLength: Int? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    bottomHelper: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        if (topLabel != null) {
            Text(
                text = topLabel,
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray700,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input Field
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val borderColor =
            when {
                !enabled -> SixpackTheme.colors.gray100
                isError -> SixpackTheme.colors.red
                isFocused -> SixpackTheme.colors.gray900
                else -> SixpackTheme.colors.gray100
            }
        val textColor =
            when {
                !enabled -> SixpackTheme.colors.gray900
                else -> SixpackTheme.colors.gray900
            }

        // TextFieldValue로 관리하되 커서를 항상 맨 끝으로 강제
        // value가 key이므로 value 변경 시 자동으로 새 TextFieldValue 생성
        var textFieldValueState by remember(value) {
            mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
        }

        val customTextSelectionColors =
            TextSelectionColors(
                handleColor = SixpackTheme.colors.blue300,
                backgroundColor = SixpackTheme.colors.blue600.copy(alpha = 0.4f),
            )

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            BasicTextField(
                value = textFieldValueState,
                onValueChange = { newValue ->

                    // 최대 길이 제한 적용
                    val newText = if (maxLength != null) {
                        newValue.text.take(maxLength)
                    } else {
                        newValue.text
                    }
                    val newLength = newText.length
                    textFieldValueState = TextFieldValue(
                        text = newText,
                        selection = TextRange(newLength),
                    )
                    onValueChange(newText)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = SixpackTheme.shapes.round8,
                        ),
                enabled = enabled,
                textStyle = SixpackTheme.typography.b2Regular.copy(color = textColor),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = keyboardType,
                    ),
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                cursorBrush = SolidColor(SixpackTheme.colors.blue600),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (textFieldValueState.text.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = SixpackTheme.typography.b2Regular,
                                color = SixpackTheme.colors.gray300,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                innerTextField()
                            }
                            trailingIcon?.invoke()
                        }
                    }
                },
            )
        }
        if (bottomHelper != null) {
            Spacer(modifier = Modifier.height(8.dp))
            bottomHelper()
        }
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
                topLabel = "라벨 텍스트",
                placeholder = "플레이스홀더",
            )

            Spacer(modifier = Modifier.height(24.dp))

            DoRunSignInputField(
                value = "입력된 텍스트",
                onValueChange = {},
                topLabel = "라벨 텍스트",
            )

            Spacer(modifier = Modifier.height(24.dp))

            DoRunSignInputField(
                value = "",
                onValueChange = {},
                topLabel = "라벨 텍스트",
                isError = true,
            )
        }
    }
}
