package com.dpm.sixpack.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex

@Composable
fun FullScreenLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().zIndex(1f),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
        )
        CircularProgressIndicator(
            color = Color.White,
        )
    }
}

@Preview
@Composable
private fun PreviewFullScreenLoadingIndicator() {
    FullScreenLoadingIndicator()
}
