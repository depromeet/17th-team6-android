package com.dpm.sixpack.presentation.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BottomLongTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = DEFAULT_TEXT_COLOR,
    enabled: Boolean = true,
) {
    TextButton(
        modifier = modifier,
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors =
            ButtonColors(
                containerColor = DEFAULT_CONTAINER_COLOR,
                contentColor = textColor,
                disabledContainerColor = DEFAULT_CONTAINER_COLOR,
                disabledContentColor = Color.Gray,
            ),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = text,
        )
    }
}

@Preview
@Composable
fun BottomLongButtonPreview() {
    BottomLongTextButton(
        textColor = DEFAULT_TEXT_COLOR,
        onClick = { },
        text = "Text",
    )
}

private val DEFAULT_CONTAINER_COLOR = Color(0xFF4C46FC)
private val DEFAULT_TEXT_COLOR = Color.White
