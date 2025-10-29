package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.my.addMyPageNavGraph
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingRoute
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
            composable<OnboardingRoute> {
                OnboardingRoute(
                    onNavigateToSignUp = {
                        // TODO SR-N SignUp Navigation 구현
                    },
                    onNavigateToSignIn = {
                        // TODO SR-N SignIn Navigation 구현
                    },
                )
            }

            addRunningSessionNavGraph(
                onNavigateToBack = navigator::popBackStack,
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                navigateToSessionReport = navigator::navigateToSessionReport,
            )

            addSessionReportNavGraph(
                onNavigateToBack = navigator::popBackStack,
            )

            addMyPageNavGraph()
        }
    }
}
