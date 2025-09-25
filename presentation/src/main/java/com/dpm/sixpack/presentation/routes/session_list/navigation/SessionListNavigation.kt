package com.dpm.sixpack.presentation.routes.session_list.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.Route
import com.dpm.sixpack.presentation.destinations.SessionListRoute
import com.dpm.sixpack.presentation.routes.home.HomeRoute
import com.dpm.sixpack.presentation.routes.session_list.SessionListRoute

fun NavController.navigateSessionList(
    navOptions: NavOptions? = null,
    totalGoalId: Long
) {
    // TODO SR-N Argument 처리
    navigate(
        route = SessionListRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.addSessionListNavGraph(
    onNavigateToSession: (sessionId: Long) -> Unit
) {
    // TODO SR-N Argument 처리
    composable<SessionListRoute> {
        SessionListRoute(onNavigateToSession = onNavigateToSession)
    }
}
