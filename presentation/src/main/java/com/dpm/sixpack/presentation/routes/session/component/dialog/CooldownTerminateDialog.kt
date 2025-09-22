package com.dpm.sixpack.presentation.routes.session.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.session.component.panel.RecordStopButton

@Composable
internal fun CooldownTerminateDialog(
    onCancelClick: () -> Unit,
    onStopClick: () -> Unit,
) {
    RunningDialog(
        onDismissRequest = {},
        title = stringResource(R.string.dialog_running_terminate_title),
        contentText = stringResource(R.string.dialog_cooldown_terminate_content),
        onCancelClick = onCancelClick,
    ) {
        RecordStopButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(56.dp),
            onClick = onStopClick,
        )
    }
}

@Preview
@Composable
private fun CooldownTerminateDialogPreview() {
    CooldownTerminateDialog(
        onCancelClick = {},
        onStopClick = {},
    )
}
