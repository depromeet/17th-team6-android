package com.dpm.sixpack.presentation.routes.running.session.component.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionUiState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.RecordState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RunningRecordPanel(
    sessionState: RunningSessionUiState.HasRecord,
    modifier: Modifier = Modifier,
    onPauseClick: () -> Unit = {},
    onResumeClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
) {
    val panelRoundedShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    Surface(
        modifier =
            modifier
                .background(SixpackTheme.colors.gray0, shape = panelRoundedShape)
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .padding(top = 12.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = SixpackTheme.colors.gray0,
                    ),
        ) {
            when (sessionState) {
                is RunningSessionUiState.Running -> {
                    MainRunningRecordGrid(recordState = sessionState.recordState)
                    Spacer(modifier = Modifier.height(32.dp))
                    DoRunDefaultButton(
                        onClick = onPauseClick,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        text = stringResource(R.string.panel_record_pause),
                    )
                }

                is RunningSessionUiState.Pause -> {
                    MainRunningRecordGrid(recordState = sessionState.recordState)
                    Spacer(modifier = Modifier.height(32.dp))
                    PausedButtons(
                        onResumeClick = onResumeClick,
                        onStopClick = onStopClick,
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
            RunningSessionUiState.Running(
                recordState =
                    RecordState(
                        currentDistance = 15400,
                        currentDuration = 1530,
                        pace = 440,
                        cadence = 154,
                    ),
            ),
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
            RunningSessionUiState.Pause(
                recordState =
                    RecordState(
                        currentDistance = 15400,
                        currentDuration = 1530,
                        pace = 440,
                        cadence = 154,
                    ),
            ),
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
    )
}
