package com.dpm.sixpack.presentation.routes.report.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.dialog.DoRunErrorScreen

@Composable
fun ReportErrorScreen(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    navigateToHome: () -> Unit = {},
) {
    var retryCount: Int by remember { mutableIntStateOf(0) }

    DoRunErrorScreen(
        modifier = modifier,
        title = title,
        description = description,
        confirmButtonText = if (retryCount < 2) confirmButtonText else stringResource(id = R.string.back_to_home),
        onConfirmClick = {
            if (retryCount < 2) {
                retryCount++
                onConfirmClick()
            } else {
                navigateToHome()
            }
        },
    )
}
