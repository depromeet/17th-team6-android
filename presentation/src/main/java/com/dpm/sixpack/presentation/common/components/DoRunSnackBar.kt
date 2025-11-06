package com.dpm.sixpack.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunSnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = SixpackTheme.colors.gray900.copy(alpha = 0.8f),
        shape = SixpackTheme.shapes.round8,
        shadowElevation = 4.dp,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = snackBarData.visuals.message,
                color = SixpackTheme.colors.gray0,
                style = SixpackTheme.typography.b1Medium,
                textAlign = TextAlign.Center,
            )

            // (선택) 액션 버튼이 있다면 표시
            snackBarData.visuals.actionLabel?.let { actionLabel ->
                TextButton(onClick = { snackBarData.performAction() }) {
                    Text(
                        text = actionLabel,
                        color = SixpackTheme.colors.gray0,
                        style = SixpackTheme.typography.b1Medium,
                    )
                }
            }
        }
    }
}
