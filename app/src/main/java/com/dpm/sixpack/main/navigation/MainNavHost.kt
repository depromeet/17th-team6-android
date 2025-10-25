package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingRoute
import com.dpm.sixpack.presentation.routes.session.navigation.addRunningSessionNavGraph
import com.dpm.sixpack.presentation.routes.sessionreport.navigation.addSessionReportNavGraph
import com.dpm.sixpack.presentation.routes.signup.navigation.addSignUpNavGraph
import com.dpm.sixpack.presentation.routes.signup.navigation.navigateSignUp
import com.dpm.sixpack.presentation.routes.terms.navigation.addTermsNavGraph
import com.dpm.sixpack.presentation.routes.terms.navigation.navigateTerms

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
            composable<OnboardingRoute> {
                OnboardingRoute(
                    onNavigateToSignUp = {
                        navigator.navController.navigateTerms()
                    },
                    onNavigateToSignIn = {
                        // TODO SR-N SignIn Navigation 구현
                    },
                )
            }

            addTermsNavGraph(
                onNavigateToSignUp = {
                    navigator.navController.navigateSignUp()
                },
                onNavigateToBack = navigator::popBackStack,
            )

            addSignUpNavGraph(
                onNavigateToHome = {
                    // TODO SR-N: Home 화면으로 이동 또는 메인 탭 네비게이션으로 이동
                },
                onNavigateToBack = navigator::popBackStack,
            )

            addRunningSessionNavGraph(
                onNavigateToBack = navigator::popBackStack,
                navigateToSessionReport = navigator::navigateToSessionReport,
            )

            addSessionReportNavGraph(
                onNavigateToBack = navigator::popBackStack,
            )
        }
    }
}
