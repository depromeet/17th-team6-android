package com.dpm.sixpack.presentation.routes.running.session

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    onSessionFinished: () -> Unit,
    sessionViewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val sessionState = sessionViewModel.collectAsState().value

    sessionViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is RunningSessionSideEffect.SessionFinish -> {
                onSessionFinished()
            }

            is RunningSessionSideEffect.UpdateRunningPath -> {
                updateNewRunningPath(sideEffect.newPathState)
            }
        }
    }

    BackHandler {
        // 뒤로가기 못하게
    }

    when (sessionState) {
        is RunningSessionUiState.Initial -> {
            // do nothing
        }

        is RunningSessionUiState.Ready -> {
            ReadyOverlay(
                modifier = Modifier.fillMaxSize(),
                readyState = sessionState,
            )
        }

        is RunningSessionUiState.Running -> {
            RunningRecordPanel(
                modifier = recordPanelModifier(panelRef),
                sessionState = sessionState,
                onPauseClick = sessionViewModel::onIntent,
                onResumeClick = sessionViewModel::onIntent,
                onStopClick = sessionViewModel::onIntent,
            )
        }

        is RunningSessionUiState.Pause -> {
            RunningRecordPanel(
                modifier = recordPanelModifier(panelRef),
                sessionState = sessionState,
                onPauseClick = sessionViewModel::onIntent,
                onResumeClick = sessionViewModel::onIntent,
                onStopClick = sessionViewModel::onIntent,
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

@SuppressLint("ModifierFactoryExtensionFunction")
fun ConstraintLayoutScope.recordPanelModifier(ref: ConstrainedLayoutReference): Modifier =
    Modifier.constrainAs(ref) {
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }
