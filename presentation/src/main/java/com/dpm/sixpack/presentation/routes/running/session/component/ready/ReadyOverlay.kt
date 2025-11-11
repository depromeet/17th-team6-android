package com.dpm.sixpack.presentation.routes.running.session.component.ready

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionUiState

@Composable
internal fun ReadyOverlay(
    readyState: RunningSessionUiState.Ready,
    modifier: Modifier = Modifier,
) {
    val primaryText = stringResource(R.string.ready_main_primary)

    BaseReadyOverlay(
        modifier = modifier,
        primaryText = primaryText,
        onlyText = readyState.onlyText,
        countdown = readyState.countdown,
    )
}

@Preview
@Composable
private fun PreviewReadyOverlay() {
    ReadyOverlay(
        readyState =
            RunningSessionUiState.Ready(
                countdown = 2,
            ),
    )
}
