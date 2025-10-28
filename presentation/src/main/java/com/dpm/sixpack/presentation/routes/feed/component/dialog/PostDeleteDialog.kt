package com.dpm.sixpack.presentation.routes.feed.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.dialog.DialogButtonType
import com.dpm.sixpack.presentation.common.components.dialog.DoRunDefaultDialog

@Composable
fun PostDeleteDialog(
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    DoRunDefaultDialog(
        title = stringResource(id = R.string.feed_dialog_delete_post_title),
        subtitle = stringResource(id = R.string.feed_dialog_delete_post_subtitle),
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        confirmButtonText = stringResource(id = R.string.feed_dialog_delete_post_confirm_button),
        onConfirmClick = onConfirmClick,
        cancelButtonText = stringResource(R.string.dialog_cancel),
        confirmButtonType = DialogButtonType.Destructive,
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
