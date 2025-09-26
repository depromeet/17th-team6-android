package com.dpm.sixpack.presentation.routes.goaledit.routes.question.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.GoalEditRoute
import com.dpm.sixpack.presentation.destinations.SessionListRoute
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.GoalEditQuestionRoute

fun NavController.navigateGoalEditQuestion(navOptions: NavOptions? = null) {
    navigate(
        route = GoalEditRoute.Question,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.addGoalEditQuestionNavGraph(
    onNavigateToBack: () -> Unit = {},
    onNavigateToGoalEditResult: () -> Unit = {},
) {
    composable<GoalEditRoute.Question> {
        GoalEditQuestionRoute(
            onNavigateToBack = onNavigateToBack,
            onNavigateToGoalEditResult = onNavigateToGoalEditResult,
        )
    }
}
