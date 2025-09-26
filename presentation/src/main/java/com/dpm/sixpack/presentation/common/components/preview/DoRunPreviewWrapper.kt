package com.dpm.sixpack.presentation.common.components.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunPreviewWrapper(content: @Composable () -> Unit) {
    SixpackTheme(isDebug = true) {
        Surface(color = SixpackTheme.colors.gray0) {
            content()
        }
    }
}
