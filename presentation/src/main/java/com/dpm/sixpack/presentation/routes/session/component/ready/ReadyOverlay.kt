package com.dpm.sixpack.presentation.routes.session.component.ready

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState

@Composable
internal fun ReadyOverlay(
    readyState: RunningSessionState.Ready,
    modifier: Modifier = Modifier,
) {
    val primaryText = stringResource(R.string.ready_main_primary)

    BaseReadyOverlay(
        modifier = modifier,
        primaryText = primaryText,
        secondaryText = "",
        onlyText = readyState.onlyText,
        countdown = readyState.countdown,
    )
}

@Preview
@Composable
private fun PreviewReadyOverlay() {
    ReadyOverlay(
        readyState =
            RunningSessionState.Ready(
                countdown = 2,
            ),
    )
}
