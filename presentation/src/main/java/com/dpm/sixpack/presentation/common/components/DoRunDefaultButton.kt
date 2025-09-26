package com.dpm.sixpack.presentation.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunDefaultButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = SixpackTheme.colors.gray0,
    containerColor: Color = SixpackTheme.colors.blue600,
    disabledTextColor: Color = SixpackTheme.colors.gray400,
    disabledContainerColor: Color = SixpackTheme.colors.gray100,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    Button(
        modifier = modifier,
        onClick = {
            onClick()
        },
        shape = SixpackTheme.shapes.round12,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                disabledContainerColor = disabledContainerColor,
            ),
        contentPadding = contentPadding,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            style = SixpackTheme.typography.b1Bold,
            color = if (enabled) textColor else disabledTextColor,
        )
    }
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
            text = "이 목표로 다시 러닝",
            enabled = false,
        )
    }
}
