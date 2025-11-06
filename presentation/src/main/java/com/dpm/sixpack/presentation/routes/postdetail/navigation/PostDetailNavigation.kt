package com.dpm.sixpack.presentation.routes.postdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.PostDetail
import com.dpm.sixpack.presentation.routes.postdetail.PostDetailRoute

fun NavController.navigateToPostDetail(feedId: Long) {
    navigate(PostDetail(feedId = feedId))
}

fun NavGraphBuilder.addPostDetailNavGraph(
    navigateToBack: () -> Unit = {},
    navigateToUserProfile: (Long) -> Unit = {},
    navigateToMyPage: () -> Unit = {},
    navigateToPostEdit: (Long) -> Unit = {},
) {
    composable<PostDetail> {
        PostDetailRoute(
            navigateToMyPage = navigateToMyPage,
            navigateToBack = navigateToBack,
            navigateToUserProfile = navigateToUserProfile,
            navigateToPostEdit = navigateToPostEdit,
        )
    }
}
