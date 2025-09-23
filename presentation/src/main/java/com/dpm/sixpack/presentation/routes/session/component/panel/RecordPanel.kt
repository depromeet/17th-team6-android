package com.dpm.sixpack.presentation.routes.session.component.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RecordUiState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RunningRecordPanel(
    sessionState: RunningSessionState.HasRecord,
    primaryInfo: String,
    secondaryInfo: String,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMain = sessionState is RunningSessionState.Main
    val isWarmUp = sessionState is RunningSessionState.WarmUp
    val isCoolDown = sessionState is RunningSessionState.CoolDown
    val isPaused = sessionState is RunningSessionState.PausedState
    val isRunning = sessionState is RunningSessionState.RunningState

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(SixpackTheme.colors.gray0, shape = SixpackTheme.shapes.round20)
                .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        // 세션 정보
        if (isMain) {
            MainSessionInfo(
                primaryInfo = primaryInfo,
                secondaryInfo = secondaryInfo,
            )
        }
        if (isWarmUp) {
            PrePostSessionInfo(
                primaryInfo = primaryInfo,
                showSkip = true,
                onSkipClick = onSkipClick,
            )
        }
        if (isCoolDown) {
            PrePostSessionInfo(
                primaryInfo = primaryInfo,
                showSkip = false,
                onSkipClick = {},
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 기록 그리드
        if (isMain) {
            MainRunningRecordGrid(
                recordUiState = sessionState.recordUiState,
            )
        }
        if (isWarmUp || isCoolDown) {
            PrePostRunningRecordGrid(
                recordUiState = sessionState.recordUiState,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 하단 버튼
        if (isRunning) {
            DoRunDefaultButton(
                onClick = { /* 기록 중지 로직 */ },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                text = "기록 중지",
            )
        }

        if (isPaused) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RecordStopButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(56.dp),
                    onClick = { /* 기록 중지 로직 */ },
                )

                Spacer(modifier = Modifier.width(10.dp))

                DoRunDefaultButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(56.dp),
                    text = stringResource(R.string.panel_record_resume),
                    onClick = { /* 기록 재개 로직 */ },
                )
            }
        }
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
                        currentDistance = "1.54km",
                        currentDuration = "00:23:17",
                        avgPace = "7'20\"",
                        cadence = "154",
                    ),
            ),
        primaryInfo = "러닝",
        secondaryInfo = "5.0km",
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
                        currentDistance = "1.54km",
                        currentDuration = "00:23:17",
                        avgPace = "7'20\"",
                        cadence = "154",
                    ),
            ),
        primaryInfo = "러닝",
        secondaryInfo = "5.0km",
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
                        currentDistance = "1.54km",
                        currentDuration = "00:23:17",
                        avgPace = "7'20\"",
                        cadence = "154",
                    ),
            ),
        primaryInfo = "쿨다운",
        secondaryInfo = "건너뛰기",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
        onSkipClick = {},
    )
}
