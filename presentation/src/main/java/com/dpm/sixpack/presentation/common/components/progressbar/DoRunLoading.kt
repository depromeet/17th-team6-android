package com.dpm.sixpack.presentation.common.components.progressbar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DoRunLoading(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = 4.dp,
        strokeCap = StrokeCap.Butt,
    )
}
