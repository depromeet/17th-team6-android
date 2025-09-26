package com.dpm.sixpack.presentation.routes.goaledit.routes.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.GoalEditRoute
import com.dpm.sixpack.presentation.routes.goaledit.routes.result.GoalEditResultRoute

fun NavController.navigateGoalEditResult(navOptions: NavOptions? = null) {
    navigate(
        route = GoalEditRoute.Result,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.addGoalEditResultNavGraph(
    onNavigateToBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    composable<GoalEditRoute.Result> {
        GoalEditResultRoute(
            onNavigateToBack = onNavigateToBack,
            onNavigateToHome = onNavigateToHome,
        )
    }
}
