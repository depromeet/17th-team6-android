package com.dpm.sixpack.presentation.routes.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.destinations.CertifiedUsers
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.PostEdit
import com.dpm.sixpack.presentation.routes.feed.FeedRoute
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.CertifiedUsersRoute
import com.dpm.sixpack.presentation.routes.feed.postedit.PostEditRoute

fun NavController.navigateToFeed(navOptions: NavOptions? = null) {
    navigate(MainRoute.Feed, navOptions)
}

fun NavController.navigateToCertifiedUsers(date: String) {
    navigate(CertifiedUsers(date = date))
}

fun NavController.navigateToPostEdit(feedId: Long) {
    navigate(PostEdit(feedId = feedId))
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

    composable<CertifiedUsers> { backStackEntry ->
        val route = backStackEntry.toRoute<CertifiedUsers>()

        CertifiedUsersRoute(
            date = route.date,
            navigateToBack = navigateToBack,
            navigateToUserProfile = navigateToUserProfile,
            navigateToMyPage = navigateToMyPage,
        )
    }

    composable<PostEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<PostEdit>()

        PostEditRoute(
            feedId = route.feedId,
            onNavigateBack = navigateToBack,
        )
    }
}
