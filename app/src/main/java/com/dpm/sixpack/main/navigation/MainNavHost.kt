package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.destinations.SignInRoute
import com.dpm.sixpack.presentation.routes.feed.navigation.addFeedNavGraph
import com.dpm.sixpack.presentation.routes.feed.navigation.navigateToCertifiedUsers
import com.dpm.sixpack.presentation.routes.friend.navigation.addFriendNavGraph
import com.dpm.sixpack.presentation.routes.friend.navigation.navigateToFriendGraph
import com.dpm.sixpack.presentation.routes.onboarding.navigation.addOnboardingNavGraph
import com.dpm.sixpack.presentation.routes.postdetail.navigation.addPostDetailNavGraph
import com.dpm.sixpack.presentation.routes.postdetail.navigation.navigateToPostDetail
import com.dpm.sixpack.presentation.routes.postedit.navigation.addPostDetailNavGraph
import com.dpm.sixpack.presentation.routes.postedit.navigation.navigateToPostEdit
import com.dpm.sixpack.presentation.routes.profilecreation.navigation.addProfileCreationNavGraph
import com.dpm.sixpack.presentation.routes.profilecreation.navigation.navigateProfileCreation
import com.dpm.sixpack.presentation.routes.report.navigation.addSessionReportNavGraph
import com.dpm.sixpack.presentation.routes.running.navigation.addRunningNavGraph
import com.dpm.sixpack.presentation.routes.running.navigation.navigateRunning
import com.dpm.sixpack.presentation.routes.signin.navigation.addSignInNavGraph
import com.dpm.sixpack.presentation.routes.signin.navigation.navigateSignIn
import com.dpm.sixpack.presentation.routes.signup.navigation.addSignUpNavGraph
import com.dpm.sixpack.presentation.routes.signup.navigation.navigateSignUp
import com.dpm.sixpack.presentation.routes.terms.navigation.addTermsNavGraph
import com.dpm.sixpack.presentation.routes.terms.navigation.navigateTerms

@Composable
internal fun MainNavHost(
    appState: SixPackAppState,
    onShowSnackBar: (String, String?) -> Unit,
    setFullScreenLoading: (Boolean) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigator = appState.navigator
    val navController = navigator.navController
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        NavHost(
            navController = navController,
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

            addSignUpNavGraph(
                onNavigateToProfileCreation = { phoneNumber ->
                    navigator.navController.navigateProfileCreation(phoneNumber = phoneNumber)
                },
                onNavigateBack = navigator::popBackStack,
            )

            addProfileCreationNavGraph(
                onNavigateToHome = {
                    navigator.navController.navigateRunning(
                        navOptions {
                            popUpTo(navigator.navController.graph.id) {
                                inclusive = true
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        },
                    )
                },
                onNavigateToBack = navigator::popBackStack,
            )

            addSignInNavGraph(
                onNavigateToHome = {
                    navController.navigateRunning(
                        navOptions {
                            popUpTo(navigator.navController.graph.id) {
                                inclusive = true
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        },
                    )
                },
                onNavigateToSignUp = { phoneNumber ->
                    navigator.navController.navigateTerms(
                        navOptions {
                            popUpTo(SignInRoute) {
                                inclusive = true
                                saveState = false
                            }
                        },
                    )
                },
                onNavigateBack = navigator::popBackStack,
            )

            addRunningNavGraph(
                onShowSnackBar = onShowSnackBar,
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                navigateToReport = navigator::navigateToSessionReport,
                navigateToBack = navigator::popBackStack,
                navigateToFriendList = navController::navigateToFriendGraph,
                showFullScreenLoading = setFullScreenLoading,
            )

            addSessionReportNavGraph(
                navigateToBack = navigator::popBackStack,
                onShowSnackBar = onShowSnackBar,
                navigateToCertification = { },
            )

            addFeedNavGraph(
                navigateToBack = { navController.popBackStack() },
                navigateToCertifiedUsers = navController::navigateToCertifiedUsers,
                navigateToPostDetail = navController::navigateToPostDetail,
                navigateToPostEdit = navController::navigateToPostEdit,
            )

            addPostDetailNavGraph(
                navigateToBack = { navController.popBackStack() },
                navigateToPostEdit = navController::navigateToPostEdit,
                navigateToUserProfile = {},
                navigateToMyPage = {},
            )

            addPostDetailNavGraph(
                navigateToBack = { navController.popBackStack() },
            )

            addFeedNavGraph()

            addFriendNavGraph(
                navController = navigator.navController,
                navigateToBack = navigator::popBackStack,
                onShowSnackBar = onShowSnackBar,
            )
        }
    }
}
