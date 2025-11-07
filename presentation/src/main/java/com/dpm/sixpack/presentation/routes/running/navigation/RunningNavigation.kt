package com.dpm.sixpack.presentation.routes.running.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.running.RunningRoute

fun NavController.navigateRunning(navOptions: NavOptions? = null) {
    navigate(MainRoute.Running, navOptions)
}

fun NavGraphBuilder.addRunningNavGraph(
    onShowSnackBar: (String, String?) -> Unit,
    showFullScreenLoading: (Boolean) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    navigateToBack: () -> Unit,
    navigateToSessionReport: () -> Unit,
    navigateToFriendList: () -> Unit,
) {
    composable<MainRoute.Running>(
        deepLinks = listOf(
            navDeepLink<MainRoute.Running>(basePath = DeepLinks.Running.START)
        )
    ) {
        RunningRoute(
            onShowSnackBar = onShowSnackBar,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            setFullScreenLoading = showFullScreenLoading,
            navigateToReport = navigateToSessionReport,
            navigateToBack = navigateToBack,
            navigateToFriendList = navigateToFriendList,
        )
    }
}
