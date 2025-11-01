package com.dpm.sixpack.presentation.routes.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.feed.FeedRoute

fun NavGraphBuilder.addFeedNavGraph(
    navigateToGroup: () -> Unit,
    navigateToAlarm: () -> Unit,
    navigateToCertifiedUserList: () -> Unit,
    navigateToUserProfile: (Long) -> Unit,
    navigateToMyPage: () -> Unit,
    navigateToPostDetail: (PostResource) -> Unit,
    navigateToUpload: () -> Unit,
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
        )
    }
}
