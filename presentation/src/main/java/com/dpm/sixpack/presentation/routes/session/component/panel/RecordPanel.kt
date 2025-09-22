package com.dpm.sixpack.presentation.routes.session.component.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    isPaused: Boolean,
    onPauseClick: (RunningSessionIntent.PauseIntent) -> Unit,
    onResumeClick: (RunningSessionIntent.ResumeIntent) -> Unit,
    onStopClick: (RunningSessionIntent.StopIntent) -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(), // 외부에서 전달된 modifier를 여기에 적용
        contentAlignment = Alignment.BottomCenter,
    ) {
        val isMain = sessionState is RunningSessionState.Main
        val isWarmUp = sessionState is RunningSessionState.WarmUp
        val isCoolDown = sessionState is RunningSessionState.CoolDown

        val primaryInfo =
            when (sessionState) {
                is RunningSessionState.WarmUp -> {
                    stringResource(R.string.running_phase_warmup_title)
                }

                is RunningSessionState.Main -> {
                    stringResource(R.string.running_phase_main_title)
                }

                is RunningSessionState.CoolDown -> {
                    stringResource(R.string.running_phase_cooldown_title)
                }
            }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(SixpackTheme.colors.gray0, shape = SixpackTheme.shapes.round20)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            // 세션 정보
            if (sessionState is RunningSessionState.Main.Running) {
                MainSessionInfo(
                    primaryInfo = primaryInfo,
                    secondaryInfo = sessionState.goalDistance,
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
            if (isPaused && sessionState is RunningSessionState.PausedState) {
                val resumeIntent =
                    when (sessionState) {
                        is RunningSessionState.WarmUp.Pause -> RunningSessionIntent.WarmUpResume
                        is RunningSessionState.Main.Pause -> RunningSessionIntent.MainRunningResume
                        is RunningSessionState.CoolDown.Pause -> RunningSessionIntent.CoolDownResume
                    }
                val stopIntent =
                    when (sessionState) {
                        is RunningSessionState.CoolDown.Pause -> RunningSessionIntent.CoolDownStop
                        is RunningSessionState.Main.Pause -> RunningSessionIntent.MainRunningStop
                        is RunningSessionState.WarmUp.Pause -> RunningSessionIntent.WarmUpStop
                    }
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
                        onClick = { onStopClick(stopIntent) },
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    DoRunDefaultButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(56.dp),
                        text = stringResource(R.string.panel_record_resume),
                        onClick = { onResumeClick(resumeIntent) },
                    )
                }
            } else {
                // 달리는 중
                if (sessionState is RunningSessionState.RunningState) {
                    val pauseIntent =
                        when (sessionState) {
                            is RunningSessionState.CoolDown.Running -> RunningSessionIntent.CoolDownPause
                            is RunningSessionState.Main.Running -> RunningSessionIntent.MainRunningPause
                            is RunningSessionState.WarmUp.Running -> RunningSessionIntent.WarmUpPause
                        }

                    DoRunDefaultButton(
                        onClick = { onPauseClick(pauseIntent) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        text = stringResource(R.string.panel_record_pause),
                    )
                }
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
//        primaryInfo = "러닝",
//        secondaryInfo = "5.0km",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
        onSkipClick = {},
        isPaused = false,
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
//        primaryInfo = "러닝",
//        secondaryInfo = "5.0km",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
        onSkipClick = {},
        isPaused = true,
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
//        primaryInfo = "쿨다운",
//        secondaryInfo = "건너뛰기",
        onPauseClick = {},
        onResumeClick = {},
        onStopClick = {},
        onSkipClick = {},
        isPaused = false,
    )
}
