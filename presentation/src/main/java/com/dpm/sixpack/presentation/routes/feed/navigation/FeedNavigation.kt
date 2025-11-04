package com.dpm.sixpack.presentation.routes.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.feed.FeedRoute

fun NavController.navigateToFeed(navOptions: NavOptions? = null) {
    navigate(MainRoute.Feed, navOptions)
}

fun NavGraphBuilder.addFeedNavGraph(
    navigateToGroup: () -> Unit = {},
    navigateToAlarm: () -> Unit = {},
    navigateToCertifiedUserList: () -> Unit = {},
    navigateToUserProfile: (Long) -> Unit = {},
    navigateToMyPage: () -> Unit = {},
    navigateToPostDetail: (PostResource) -> Unit = {},
    navigateToUpload: () -> Unit = {},
    navigateToPostEdit: (PostResource) -> Unit = {},
) {
    composable<MainRoute.Feed> {
        FeedRoute(
            navigateToGroup = navigateToGroup,
            navigateToAlarm = navigateToAlarm,
            navigateToCertifiedUserList = navigateToCertifiedUserList,
            navigateToUserProfile = navigateToUserProfile,
            navigateToMyPage = navigateToMyPage,
            navigateToPostDetail = navigateToPostDetail,
            navigateToPostUpload = navigateToUpload,
            navigateToPostEdit = navigateToPostEdit,
        )
    }
}
