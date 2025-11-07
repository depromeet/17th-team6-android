package com.dpm.sixpack.presentation.routes.postedit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
import com.dpm.sixpack.presentation.destinations.PostEdit
import com.dpm.sixpack.presentation.routes.postedit.PostEditRoute

fun NavController.navigateToPostEdit(feedId: Long) {
    navigate(PostEdit(feedId = feedId))
}

fun NavGraphBuilder.addPostDetailNavGraph(navigateToBack: () -> Unit = {}) {
    composable<PostEdit>(
        deepLinks = listOf(
            navDeepLink<PostEdit>(basePath = DeepLinks.Feed.UPLOAD)
        )
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<PostEdit>()

        PostEditRoute(
            feedId = route.feedId,
            onNavigateBack = navigateToBack,
        )
    }
}
