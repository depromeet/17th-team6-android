package com.dpm.sixpack.presentation.routes.session_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.session_list.contract.SessionListIntent
import com.dpm.sixpack.presentation.routes.session_list.ui.screen.SessionListScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SessionListRoute(
    modifier: Modifier = Modifier,
    viewModel: SessionListViewModel = hiltViewModel(),
    onNavigateToSession: (sessionId: Long) -> Unit = {}
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->

    }

    SessionListScreen(
        modifier = modifier,
        screenState = screenState,
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
        }
    )
}
