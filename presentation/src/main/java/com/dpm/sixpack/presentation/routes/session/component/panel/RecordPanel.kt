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
    onPauseClick: (RunningSessionIntent.PauseIntent) -> Unit,
    onResumeClick: (RunningSessionIntent.ResumeIntent) -> Unit,
    onStopClick: (RunningSessionIntent.StopIntent) -> Unit,
    onSkipClick: () -> Unit,
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
                    .background(SixpackTheme.colors.gray0, shape = SixpackTheme.shapes.round20)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            when (sessionState) {
                // --- WarmUp 상태 ---
                is RunningSessionState.WarmUp.Running -> {
                    PrePostSessionInfo(
                        primaryInfo = stringResource(R.string.running_phase_warmup_title),
                        showSkip = true,
                        onSkipClick = onSkipClick,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrePostRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    RunningButton(onPauseClick = { onPauseClick(RunningSessionIntent.WarmUpPause) })
                }

                is RunningSessionState.WarmUp.Pause -> {
                    PrePostSessionInfo(
                        primaryInfo = stringResource(R.string.running_phase_warmup_title),
                        showSkip = true,
                        onSkipClick = onSkipClick,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrePostRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    PausedButtons(
                        onResumeClick = { onResumeClick(RunningSessionIntent.WarmUpResume) },
                        onStopClick = { onStopClick(RunningSessionIntent.WarmUpStop) },
                    )
                }

                // --- Main Running 상태 ---
                is RunningSessionState.Main.Running -> {
                    MainSessionInfo(
                        primaryInfo = stringResource(R.string.running_phase_main_title),
                        secondaryInfo = sessionState.goalDistance,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MainRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    RunningButton(onPauseClick = { onPauseClick(RunningSessionIntent.MainRunningPause) })
                }

                is RunningSessionState.Main.Pause -> {
                    MainSessionInfo(
                        primaryInfo = stringResource(R.string.running_phase_main_title),
                        secondaryInfo = sessionState.goalDistance,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MainRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    PausedButtons(
                        onResumeClick = { onResumeClick(RunningSessionIntent.MainRunningResume) },
                        onStopClick = { onStopClick(RunningSessionIntent.MainRunningStop) },
                    )
                }

                // --- CoolDown 상태 ---
                is RunningSessionState.CoolDown.Running -> {
                    PrePostSessionInfo(
                        primaryInfo = stringResource(R.string.running_phase_cooldown_title),
                        showSkip = false,
                        onSkipClick = {},
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrePostRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    RunningButton(onPauseClick = { onPauseClick(RunningSessionIntent.CoolDownPause) })
                }

                is RunningSessionState.CoolDown.Pause -> {
                    PrePostSessionInfo(
                        primaryInfo = stringResource(R.string.running_phase_cooldown_title),
                        showSkip = false,
                        onSkipClick = {},
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrePostRunningRecordGrid(recordUiState = sessionState.recordUiState)
                    Spacer(modifier = Modifier.height(32.dp))
                    PausedButtons(
                        onResumeClick = { onResumeClick(RunningSessionIntent.CoolDownResume) },
                        onStopClick = { onStopClick(RunningSessionIntent.CoolDownStop) },
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
            RunningSessionState.Main.Running(
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
        onSkipClick = {},
    )
}

@Preview
@Composable
private fun PreviewMainRunningStatsPanelPause() {
    RunningRecordPanel(
        sessionState =
            RunningSessionState.Main.Pause(
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
        onSkipClick = {},
    )
}

@Preview
@Composable
private fun PreviewPrePostRunningStatsPanel() {
    RunningRecordPanel(
        sessionState =
            RunningSessionState.WarmUp.Running(
                recordUiState =
                    RecordUiState(
                        currentDistance = 15400,
                        currentDuration = 1530,
                        avgPace = 440,
                        cadence = 154,
                    ),
            ),
//        primaryInfo = "쿨다운",
//        secondaryInfo = "건너뛰기",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
        onSkipClick = {},
    )
}
