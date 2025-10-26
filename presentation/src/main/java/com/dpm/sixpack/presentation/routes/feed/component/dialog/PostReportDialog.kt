package com.dpm.sixpack.presentation.routes.feed.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.theme.SixpackTheme


@Composable
fun PostReportDialog(
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    FeedDefaultDialog(
        title = "해당 게시물을 신고할까요?",
        subtitle = "심사를 거쳐 게시물을 삭제해드립니다.",
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        confirmButtonText = "신고하기",
        onConfirmClick = onConfirmClick,
        confirmButtonTextColor =  SixpackTheme.colors.red   ,
        confirmButtonContainerColor = SixpackTheme.colors.redLight
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
