package com.dpm.sixpack.presentation.routes.feed.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun PostDeleteDialog(
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    FeedDefaultDialog(
        title = "해당 게시물을 삭제할까요?",
        subtitle = "한 번 삭제되면 복구하기 어려워요.",
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        confirmButtonText = "삭제하기",
        onConfirmClick = onConfirmClick,
        confirmButtonTextColor =  SixpackTheme.colors.red   ,
        confirmButtonContainerColor = SixpackTheme.colors.redLight
    )
}

@Preview
@Composable
fun PostDeleteDialogPreview() {
    PostDeleteDialog(
        onDismissRequest = {},
        onCancelClick = {},
        onConfirmClick = {},
    )
}
