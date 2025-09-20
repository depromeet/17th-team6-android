package com.dpm.sixpack.presentation.routes.session.component.ready

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState

@Composable
internal fun ReadyOverlay(
    readyState: RunningSessionState.ReadyState,
    modifier: Modifier = Modifier,
) {
    val primaryText =
        when (readyState) {
            is RunningSessionState.WarmUp.Ready -> {
                stringResource(R.string.ready_warmup_primary)
            }

            is RunningSessionState.Main.Ready -> {
                stringResource(R.string.ready_main_primary)
            }

            is RunningSessionState.CoolDown.Ready -> {
                stringResource(R.string.ready_cooldown_primary)
            }
        }

    val secondaryText =
        when (readyState) {
            is RunningSessionState.WarmUp.Ready -> {
                stringResource(R.string.ready_warmup_secondary)
            }

            is RunningSessionState.Main.Ready -> {
                stringResource(R.string.ready_main_secondary)
            }

            is RunningSessionState.CoolDown.Ready -> {
                stringResource(R.string.ready_cooldown_secondary)
            }
        }

    BaseReadyOverlay(
        modifier = modifier,
        primaryText = primaryText,
        secondaryText = secondaryText,
        countdown = readyState.countdown,
    )
}
