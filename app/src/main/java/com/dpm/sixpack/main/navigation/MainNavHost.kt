package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.routes.onboarding.navigation.addOnboardingNavGraph
import com.dpm.sixpack.presentation.routes.profilecreation.navigation.addProfileCreationNavGraph
import com.dpm.sixpack.presentation.routes.profilecreation.navigation.navigateProfileCreation
import com.dpm.sixpack.presentation.routes.running.navigation.addRunningSessionNavGraph
import com.dpm.sixpack.presentation.routes.sessionreport.navigation.addSessionReportNavGraph
import com.dpm.sixpack.presentation.routes.signin.navigation.addSignInNavGraph
import com.dpm.sixpack.presentation.routes.signin.navigation.navigateSignIn
import com.dpm.sixpack.presentation.routes.signup.navigation.addSignUpNavGraph
import com.dpm.sixpack.presentation.routes.signup.navigation.navigateSignUp
import com.dpm.sixpack.presentation.routes.terms.navigation.addTermsNavGraph
import com.dpm.sixpack.presentation.routes.terms.navigation.navigateTerms

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
                onNavigateToSignUp = {
                    navigator.navController.navigateTerms()
                },
                onNavigateToSignIn = {
                    navigator.navController.navigateSignIn()
                },
            )

            addTermsNavGraph(
                onNavigateToSignUp = {
                    navigator.navController.navigateSignUp()
                },
                onNavigateToBack = navigator::popBackStack,
            )

            addSignInNavGraph(
                onNavigateToHome = {
                    // TODO SR-N: Home 화면으로 이동 또는 메인 탭 네비게이션으로 이동
                },
                onNavigateToSignUp = { phoneNumber ->
                    navigator.navController.navigateSignUp()
                },
                onNavigateBack = navigator::popBackStack,
            )

            addSignUpNavGraph(
                onNavigateToProfileCreation = {
                    navigator.navController.navigateProfileCreation()
                },
                onNavigateToSignIn = { phoneNumber ->
                    // TODO SR-N 계정찾기 화면으로 이동
                },
                onNavigateToBack = navigator::popBackStack,
            )

            addProfileCreationNavGraph(
                onNavigateToHome = {
                    // TODO SR-N: Home 화면으로 이동 또는 메인 탭 네비게이션으로 이동
                },
                onNavigateToBack = navigator::popBackStack,
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
