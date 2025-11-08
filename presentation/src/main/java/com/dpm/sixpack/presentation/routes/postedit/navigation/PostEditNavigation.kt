package com.dpm.sixpack.presentation.routes.postedit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
import com.dpm.sixpack.presentation.destinations.PostEdit
import com.dpm.sixpack.presentation.routes.postedit.PostEditRoute

fun NavController.navigateToPostEdit(feedId: Long) {
    navigate(PostEdit(feedId = feedId))
}

fun NavGraphBuilder.addPostEditNavGraph(navigateToBack: () -> Unit = {}) {
    composable<PostEdit>(
        deepLinks =
            listOf(
                navDeepLink<PostEdit>(basePath = DeepLinks.Feed.UPLOAD),
            ),
    ) {
        PostEditRoute(
            navigateBack = navigateToBack,
        )
    }
}
