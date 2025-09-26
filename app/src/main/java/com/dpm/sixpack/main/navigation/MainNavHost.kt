package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.destinations.GoalEditRoute
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.navigation.addGoalEditQuestionNavGraph
import com.dpm.sixpack.presentation.routes.goaledit.routes.result.navigation.addGoalEditResultNavGraph
import com.dpm.sixpack.presentation.routes.home.navigation.addHomeNavGraph
import com.dpm.sixpack.presentation.routes.onboarding.navigation.addOnboardingNavGraph
import com.dpm.sixpack.presentation.routes.session.navigation.addRunningSessionNavGraph
import com.dpm.sixpack.presentation.routes.sessionlist.navigation.addSessionListNavGraph

@Composable
internal fun MainNavHost(
    appState: SixPackAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navigator = appState.navigator

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
        ) {
            addOnboardingNavGraph(
                navController = navigator.navController,
                navigateToHome = navigator::navigateToHome,
            )

            addHomeNavGraph(
                onNavigateToSessionList = navigator::navigateToSessionList,
                onNavigateToSession = {
                    navigator.navigateToRunningSession()
                },
                onNavigateToGoalEdit = navigator::navigateToGoalEditQuestion,
            )

            addSessionListNavGraph(
                onNavigateToBack = navigator::popBackStack,
                onNavigateToGoalEdit = navigator::navigateToGoalEditQuestion,
                onNavigateToSession = {
                    navigator.navigateToRunningSession()
                },
            )

            addGoalEditQuestionNavGraph(
                onNavigateToBack = navigator::popBackStack,
                onNavigateToGoalEditResult = navigator::navigateToGoalEditResult,
            )

            addGoalEditResultNavGraph(
                onNavigateToBack = navigator::popBackStack,
                onNavigateToHome = {
                    navigator.navigateToHome(
                        navOptions {
                            popUpTo(GoalEditRoute) {
                                inclusive = true
                            }
                        },
                    )
                },
            )

            addRunningSessionNavGraph(
                onNavigateToBack = navigator::popBackStack,
            )
        }
    }
}
