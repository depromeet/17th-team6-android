package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.destinations.SignInRoute
import com.dpm.sixpack.presentation.navigation.MainNavTab
import com.dpm.sixpack.presentation.routes.feed.navigation.addFeedNavGraph
import com.dpm.sixpack.presentation.routes.feed.navigation.navigateToCertifiableRecord
import com.dpm.sixpack.presentation.routes.feed.navigation.navigateToCertifiedUsers
import com.dpm.sixpack.presentation.routes.mypage.navigation.addMyPageNavGraph
import com.dpm.sixpack.presentation.routes.onboarding.navigation.addOnboardingNavGraph
import com.dpm.sixpack.presentation.routes.postdetail.navigation.addPostDetailNavGraph
import com.dpm.sixpack.presentation.routes.postdetail.navigation.navigateToPostDetail
import com.dpm.sixpack.presentation.routes.postedit.navigation.addPostEditNavGraph
import com.dpm.sixpack.presentation.routes.postedit.navigation.navigateToPostEdit
import com.dpm.sixpack.presentation.routes.postupload.navigation.addPostUploadNavGraph
import com.dpm.sixpack.presentation.routes.postupload.navigation.navigateToPostUpload
import com.dpm.sixpack.presentation.routes.profilecreation.navigation.addProfileCreationNavGraph
import com.dpm.sixpack.presentation.routes.profilecreation.navigation.navigateProfileCreation
import com.dpm.sixpack.presentation.routes.running.navigation.addRunningSessionNavGraph
import com.dpm.sixpack.presentation.routes.running.navigation.navigateRunningSession
import com.dpm.sixpack.presentation.routes.sessionreport.navigation.addSessionReportNavGraph
import com.dpm.sixpack.presentation.routes.settings.navigation.addSettingsNavGraph
import com.dpm.sixpack.presentation.routes.settings.navigation.navigateToSettings
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
                    navigator.navController.navigateRunningSession(
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
                    navigator.navController.navigateRunningSession(
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

            addRunningSessionNavGraph(
                onNavigateToBack = navigator::popBackStack,
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                navigateToSessionReport = navigator::navigateToSessionReport,
                showFullScreenLoading = setFullScreenLoading,
            )

            addSessionReportNavGraph(
                onNavigateToBack = { navigator::popBackStack },
            )

            addFeedNavGraph(
                navigateToBack = { navController.popBackStack() },
                navigateToCertifiedUsers = navController::navigateToCertifiedUsers,
                navigateToPostDetail = navController::navigateToPostDetail,
                navigateToPostEdit = navController::navigateToPostEdit,
                navigateToCertifiableRecord = navController::navigateToCertifiableRecord,
                navigateToPostUpload = navController::navigateToPostUpload,
            )

            addPostDetailNavGraph(
                navigateToBack = { navController.popBackStack() },
                navigateToPostEdit = navController::navigateToPostEdit,
                navigateToUserProfile = {},
                navigateToMyPage = {},
            )

            addPostEditNavGraph(
                navigateBack = { navController.popBackStack() },
            )

            addPostUploadNavGraph(
                navigateBack = { navController.popBackStack() },
                navigateToFeed = { navigator.navigate(MainNavTab.FEED) },
            )

            addMyPageNavGraph(
                onNavigateToSettings = {
                    navController.navigateToSettings()
                },
                onNavigateToPostDetail = { id ->
                    // TODO: Navigate to post detail
                },
                onNavigateToRecordDetail = { id ->
                    // TODO: Navigate to record detail
                },
            )

            addSettingsNavGraph(
                onNavigateBack = navigator::popBackStack,
                onShowSnackbar = onShowSnackbar,
                navController = navController,
            )
        }
    }
}
