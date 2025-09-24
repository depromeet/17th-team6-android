package com.dpm.sixpack.presentation.common.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DoRunTopBarSlot(
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(44.dp),
    ) {
        if (leadingContent != null) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                leadingContent()
            }
        }

        if (content != null) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                content()
            }
        }

        if (trailingContent != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                trailingContent()
            }
        }
    }
}
