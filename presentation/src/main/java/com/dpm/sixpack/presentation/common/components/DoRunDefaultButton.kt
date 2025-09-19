package com.dpm.sixpack.presentation.common.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunDefaultButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = SixpackTheme.colors.gray0,
    containerColor: Color = SixpackTheme.colors.blue600,
) {
    TextButton(
        modifier = modifier,
        onClick = {
            onClick()
        },
        shape = SixpackTheme.shapes.round12,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
    ) {
        Text(
            text = buttonText,
            style = SixpackTheme.typography.b1Bold,
            color = textColor,
        )
    }
}

@Preview
@Composable
private fun BottomLongButtonPreview() {
    DoRunDefaultButton(
        onClick = { },
        buttonText = "Text",
    )
}
