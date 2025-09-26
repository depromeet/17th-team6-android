package com.dpm.sixpack.presentation.routes.goaledit.routes.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SessionListRoute
import com.dpm.sixpack.presentation.routes.goaledit.routes.result.GoalEditResultRoute

fun NavController.navigateGoalEditResult(navOptions: NavOptions? = null) {
    navigate(
        route = SessionListRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.addGoalEditResultNavGraph(
    onNavigateToBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    composable<SessionListRoute> {
        GoalEditResultRoute(
            onNavigateToBack = onNavigateToBack,
            onNavigateToHome = onNavigateToHome,
        )
    }
}
