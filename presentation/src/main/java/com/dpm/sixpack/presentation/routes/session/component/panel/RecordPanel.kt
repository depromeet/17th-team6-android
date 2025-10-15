package com.dpm.sixpack.presentation.routes.session.component.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RecordUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RunningRecordPanel(
    sessionState: RunningSessionState.HasRecord,
    onPauseClick: (RunningSessionIntent.RunningPause) -> Unit,
    onResumeClick: (RunningSessionIntent.RunningResume) -> Unit,
    onStopClick: (RunningSessionIntent.RunningStop) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(SixpackTheme.colors.gray0, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .padding(top = 12.dp),
        ) {
            when (sessionState) {
                is RunningSessionState.Running -> {
                    MainRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    RunningButton(onPauseClick = { onPauseClick(RunningSessionIntent.RunningPause) })
                }

                is RunningSessionState.Pause -> {
                    MainRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    PausedButtons(
                        onResumeClick = { onResumeClick(RunningSessionIntent.RunningResume) },
                        onStopClick = { onStopClick(RunningSessionIntent.RunningStop) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RunningButton(onPauseClick: () -> Unit) {
    DoRunDefaultButton(
        onClick = onPauseClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        text = stringResource(R.string.panel_record_pause),
    )
}

@Composable
private fun PausedButtons(
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        RecordStopButton(
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            onClick = onStopClick,
        )
        DoRunDefaultButton(
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            text = stringResource(R.string.panel_record_resume),
            onClick = onResumeClick,
        )
    }
}

@Preview
@Composable
private fun PreviewMainRunningStatsPanel() {
    RunningRecordPanel(
        sessionState =
            RunningSessionState.Running(
                recordUiState =
                    RecordUiState(
                        currentDistance = 15400,
                        currentDuration = 1530,
                        avgPace = 440,
                        cadence = 154,
                    ),
            ),
//        primaryInfo = "러닝",
//        secondaryInfo = "5.0km",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
    )
}

@Preview
@Composable
private fun PreviewMainRunningStatsPanelPause() {
    RunningRecordPanel(
        sessionState =
            RunningSessionState.Pause(
                recordUiState =
                    RecordUiState(
                        currentDistance = 15400,
                        currentDuration = 1530,
                        avgPace = 440,
                        cadence = 154,
                    ),
            ),
//        primaryInfo = "러닝",
//        secondaryInfo = "5.0km",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
    )
}
