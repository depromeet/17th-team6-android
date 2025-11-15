package com.dpm.sixpack.presentation.routes.running.session.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.running.session.component.panel.RecordStopConfirmButton

@Composable
internal fun RunningStopDialog(
    onCancelClick: () -> Unit,
    onStopConfirmClick: () -> Unit,
) {
    RunningDialog(
        onDismissRequest = {},
        title = stringResource(R.string.dialog_running_stop_title),
        contentText = stringResource(R.string.dialog_running_stop_content),
        onCancelClick = onCancelClick,
    ) {
        RecordStopConfirmButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(56.dp),
            onClick = onStopConfirmClick,
        )
    }
}

@Preview
@Composable
private fun RunningStopDialogPreview() {
    RunningStopDialog(
        onCancelClick = {},
        onStopConfirmClick = {},
    )
}
