package com.dpm.sixpack.presentation.routes.session.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton

@Composable
internal fun WarmUpSkipDialog(
    onCancelClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    RunningDialog(
        onDismissRequest = {},
        title = stringResource(R.string.dialog_warmup_skip_title),
        contentText = stringResource(R.string.dialog_warmup_skip_content),
        onCancelClick = onCancelClick,
    ) {
        DoRunDefaultButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(56.dp),
            text = stringResource(R.string.panel_skip),
            onClick = onSkipClick,
        )
    }
}

@Preview
@Composable
private fun WarmUpSkipDialogPreview() {
    WarmUpSkipDialog(
        onCancelClick = {},
        onSkipClick = {},
    )
}
