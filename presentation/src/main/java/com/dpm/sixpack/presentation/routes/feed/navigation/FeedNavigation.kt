package com.dpm.sixpack.presentation.routes.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.feed.FeedRoute

fun NavGraphBuilder.addFeedNavGraph(
    onNavigateToGroup: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    onNavigateToCertifiedUserList: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToPostDetail: (Int) -> Unit,
) {
    composable<MainRoute.Feed> {
        FeedRoute(
            onNavigateToGroup = onNavigateToGroup,
            onNavigateToAlarm = onNavigateToAlarm,
            onNavigateToCertifiedUserList = onNavigateToCertifiedUserList,
            onNavigateToUserProfile = onNavigateToUserProfile,
            onNavigateToMyPage = onNavigateToMyPage,
            onNavigateToPostDetail = onNavigateToPostDetail,
        )
    }
}
