package com.dpm.sixpack.presentation.routes.feed.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun PostReportDialog(
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    FeedDefaultDialog(
        title = stringResource(id = R.string.feed_dialog_report_post_title),
        subtitle = stringResource(id = R.string.feed_dialog_report_post_subtitle),
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        confirmButtonText = stringResource(id = R.string.feed_dialog_report_post_confirm_button),
        onConfirmClick = onConfirmClick,
        confirmButtonTextColor = SixpackTheme.colors.red,
        confirmButtonContainerColor = SixpackTheme.colors.redLight,
    )
}

@Preview
@Composable
fun PostReportDialogPreview() {
    PostReportDialog(
        onDismissRequest = {},
        onCancelClick = {},
        onConfirmClick = {},
    )
}
