package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.onboarding.navigation.addOnboardingNavGraph
import com.dpm.sixpack.presentation.routes.running.navigation.addRunningSessionNavGraph
import com.dpm.sixpack.presentation.routes.sessionreport.navigation.addSessionReportNavGraph

@Composable
internal fun MainNavHost(
    appState: SixPackAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
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
                navigateToHome = {
                    navigator.navigateToHome(
                        navOptions {
                            popUpTo(OnboardingRoute.Onboarding) {
                                inclusive = true
                            }
                        },
                    )
                },
            )

            addRunningSessionNavGraph(
                onNavigateToBack = navigator::popBackStack,
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                navigateToSessionReport = navigator::navigateToSessionReport,
            )

            addSessionReportNavGraph(
                onNavigateToBack = navigator::popBackStack,
            )
        }
    }
}
