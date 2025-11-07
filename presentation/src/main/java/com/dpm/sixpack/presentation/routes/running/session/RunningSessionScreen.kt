package com.dpm.sixpack.presentation.routes.running.session

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.running.session.component.dialog.RunningStopDialog
import com.dpm.sixpack.presentation.routes.running.session.component.panel.RunningRecordPanel
import com.dpm.sixpack.presentation.routes.running.session.component.ready.ReadyOverlay
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.running.session.contract.RunningSessionUiState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun ConstraintLayoutScope.RunningSessionScreen(
    panelRef: ConstrainedLayoutReference,
    updateNewRunningPath: (PathState) -> Unit,
    onSessionFinish: () -> Unit,
    setFullScreenLoading: (Boolean) -> Unit,
    sessionViewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val sessionState = sessionViewModel.collectAsState().value

    sessionViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is RunningSessionSideEffect.SessionFinish -> onSessionFinish()
            is RunningSessionSideEffect.UpdateRunningPath -> updateNewRunningPath(sideEffect.newPathState)
        }
    }

    BackHandler {
        // 뒤로가기 못하게
    }

    LaunchedEffect(Unit) {
        sessionViewModel.onIntent(RunningSessionIntent.SessionStart)
    }

    when (sessionState) {
        is RunningSessionUiState.Initial -> {
            setFullScreenLoading(true)
        }

        is RunningSessionUiState.Ready -> {
            setFullScreenLoading(false)
            ReadyOverlay(
                modifier =
                    Modifier
                        .constrainAs(createRef()) {
                            applyReadyOverlayConstraints()
                        },
                readyState = sessionState,
            )
        }

        is RunningSessionUiState.Running -> {
            RunningRecordPanel(
                modifier =
                    Modifier.constrainAs(panelRef) {
                        applyPanelConstraints()
                    },
                sessionState = sessionState,
                onPauseClick = {
                    sessionViewModel.onIntent(RunningSessionIntent.RunningPause)
                },
                onResumeClick = {
                    sessionViewModel.onIntent(RunningSessionIntent.RunningResume)
                },
                onStopClick = {
                    sessionViewModel.onIntent(RunningSessionIntent.RunningStop)
                },
            )
        }

        is RunningSessionUiState.Pause -> {
            RunningRecordPanel(
                modifier =
                    Modifier.constrainAs(panelRef) {
                        applyPanelConstraints()
                    },
                sessionState = sessionState,
                onPauseClick = {
                    sessionViewModel.onIntent(RunningSessionIntent.RunningPause)
                },
                onResumeClick = {
                    sessionViewModel.onIntent(RunningSessionIntent.RunningResume)
                },
                onStopClick = {
                    sessionViewModel.onIntent(RunningSessionIntent.RunningStop)
                },
            )

            if (sessionState.showStopSessionConfirmDialog) {
                RunningStopDialog(
                    onCancelClick = {
                        sessionViewModel.onIntent(RunningSessionIntent.RunningStopCancel)
                    },
                    onStopConfirmClick = {
                        sessionViewModel.onIntent(RunningSessionIntent.RunningStopConfirm)
                    },
                )
            }
        }
    }
}

private fun ConstrainScope.applyPanelConstraints() {
    bottom.linkTo(parent.bottom)
    start.linkTo(parent.start)
    end.linkTo(parent.end)
    width = Dimension.fillToConstraints
}

private fun ConstrainScope.applyReadyOverlayConstraints() {
    top.linkTo(parent.top)
    bottom.linkTo(parent.bottom)
    start.linkTo(parent.start)
    end.linkTo(parent.end)
    width = Dimension.fillToConstraints
    height = Dimension.fillToConstraints
}
