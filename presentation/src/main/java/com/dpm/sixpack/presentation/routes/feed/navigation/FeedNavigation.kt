package com.dpm.sixpack.presentation.routes.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.CertifiedUsers
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.feed.FeedRoute
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.CertifiedUsersRoute

fun NavController.navigateToFeed(navOptions: NavOptions? = null) {
    navigate(MainRoute.Feed, navOptions)
}

fun NavController.navigateToCertifiedUsers(date: String) {
    navigate(CertifiedUsers(date = date))
}

fun NavGraphBuilder.addFeedNavGraph(
    navigateToBack: () -> Unit = {},
    navigateToGroup: () -> Unit = {},
    navigateToAlarm: () -> Unit = {},
    navigateToUserProfile: (Long) -> Unit = {},
    navigateToMyPage: () -> Unit = {},
    navigateToPostDetail: (Long) -> Unit = {},
    navigateToUpload: () -> Unit = {},
    navigateToPostEdit: (Long) -> Unit = {},
    navigateToCertifiedUsers: (String) -> Unit = {},
) {
    composable<MainRoute.Feed> {
        FeedRoute(
            navigateToGroup = navigateToGroup,
            navigateToAlarm = navigateToAlarm,
            navigateToCertifiedUserList = navigateToCertifiedUsers,
            navigateToUserProfile = navigateToUserProfile,
            navigateToMyPage = navigateToMyPage,
            navigateToPostDetail = navigateToPostDetail,
            navigateToPostUpload = navigateToUpload,
            navigateToPostEdit = navigateToPostEdit,
        )
    }

    composable<CertifiedUsers> {
        CertifiedUsersRoute(
            navigateToBack = navigateToBack,
            navigateToUserProfile = navigateToUserProfile,
            navigateToMyPage = navigateToMyPage,
        )
    }
}
