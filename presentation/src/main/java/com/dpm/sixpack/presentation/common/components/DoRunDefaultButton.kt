package com.dpm.sixpack.presentation.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.progressbar.DoRunLoading
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunDefaultButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingSize: Dp = 24.dp,
    textStyle: TextStyle = SixpackTheme.typography.b1Bold,
    textColor: Color = SixpackTheme.colors.gray0,
    containerShape: Shape = SixpackTheme.shapes.round12,
    containerColor: Color = SixpackTheme.colors.blue600,
    disabledTextColor: Color = SixpackTheme.colors.gray400,
    disabledContainerColor: Color = SixpackTheme.colors.gray100,
    buttonContentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    textPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    style: ButtonStyle = ButtonStyle.PRIMARY,
) {
    val (finalTextColor, finalContainerColor, finalDisabledTextColor, finalDisabledContainerColor) =
        when (style) {
            ButtonStyle.PRIMARY ->
                ButtonColorScheme(textColor, containerColor, disabledTextColor, disabledContainerColor)

            ButtonStyle.SECONDARY ->
                ButtonColorScheme(
                    SixpackTheme.colors.gray900,
                    SixpackTheme.colors.gray200,
                    SixpackTheme.colors.gray400,
                    SixpackTheme.colors.gray100,
                )
        }

    Button(
        modifier = modifier,
        onClick = {
            if (!isLoading) {
                onClick()
            }
        },
        shape = containerShape,
        enabled = enabled && !isLoading,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = finalContainerColor,
                disabledContainerColor = finalDisabledContainerColor,
            ),
        contentPadding = buttonContentPadding,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(textPadding),
                text = text,
                style = textStyle,
                color =
                    if (isLoading) {
                        finalDisabledContainerColor
                    } else {
                        if (enabled) finalTextColor else finalDisabledTextColor
                    },
            )
            if (isLoading) {
                DoRunLoading(
                    size = loadingSize,
                    color = finalTextColor,
                )
            }
        }
    }
}

data class ButtonColorScheme(
    val textColor: Color,
    val containerColor: Color,
    val disabledTextColor: Color,
    val disabledContainerColor: Color,
)

enum class ButtonStyle {
    PRIMARY,
    SECONDARY,
}

@Preview
@Composable
private fun BottomLongButtonEnabledPreview() {
    DoRunPreviewWrapper {
        DoRunDefaultButton(
            onClick = { },
            text = "이 목표로 다시 러닝",
        )
    }
}

@Preview
@Composable
private fun BottomLongButtonDisabledPreview() {
    DoRunPreviewWrapper {
        DoRunDefaultButton(
            onClick = { },
            isLoading = true,
            text = "이 목표로 다시 러닝",
            enabled = false,
        )
    }
}
