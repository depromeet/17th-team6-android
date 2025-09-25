package com.dpm.sixpack.presentation.routes.sessionlist

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.sessionlist.contract.SessionListIntent
import com.dpm.sixpack.presentation.routes.sessionlist.contract.SessionListSideEffect
import com.dpm.sixpack.presentation.routes.sessionlist.ui.screen.SessionListScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SessionListRoute(
    modifier: Modifier = Modifier,
    viewModel: SessionListViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit = {},
    onNavigateToGoalEdit: (goalId: Long) -> Unit = {},
    onNavigateToSession: (sessionId: Long) -> Unit = {},
) {
    val screenState by viewModel.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val previousSessionFirstErrorMessage = stringResource(R.string.home_goal_list_start_condition)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SessionListSideEffect.NavigateBack -> {
                onNavigateToBack()
            }

            is SessionListSideEffect.NavigateToGoalEdit -> {
                onNavigateToGoalEdit(sideEffect.goalId)
            }

            is SessionListSideEffect.NavigateToSession -> {
                onNavigateToSession(sideEffect.sessionId)
            }

            is SessionListSideEffect.ShowPreviousSessionFirstErrorMessage -> {
                snackBarHostState.showSnackbar(message = previousSessionFirstErrorMessage)
            }
        }
    }

    SessionListScreen(
        screenState = screenState,
        modifier = modifier,
        snackBarHostState = snackBarHostState,
        onClickBackNavigation = {
            viewModel.onIntent(SessionListIntent.NavigateBackClick)
        },
        onClickEditGoal = {
            viewModel.onIntent(SessionListIntent.GoalEditClick)
        },
        onClickSessionItem = {
            viewModel.onIntent(SessionListIntent.RunningSessionClick(it))
        },
        onClickStartPreviousSession = {
            viewModel.onIntent(SessionListIntent.StartRunningSessionClick(it))
        },
    )
}
