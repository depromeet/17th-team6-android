package com.dpm.sixpack.presentation.routes.sessionlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SessionListRoute
import com.dpm.sixpack.presentation.routes.sessionlist.SessionListRoute

fun NavController.navigateSessionList(
    navOptions: NavOptions? = null,
    totalGoalId: Long,
) {
    // TODO SR-N Argument 처리
    navigate(
        route = SessionListRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.addSessionListNavGraph(
    onNavigateToBack: () -> Unit = {},
    onNavigateToGoalEdit: (goalId: Long) -> Unit = {},
    onNavigateToSession: (sessionId: Long) -> Unit = {},
) {
    // TODO SR-N Argument 처리
    composable<SessionListRoute> {
        SessionListRoute(
            onNavigateToBack = onNavigateToBack,
            onNavigateToGoalEdit = onNavigateToGoalEdit,
            onNavigateToSession = onNavigateToSession,
        )
    }
}
