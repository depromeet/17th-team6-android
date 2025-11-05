package com.dpm.sixpack.presentation.routes.postedit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.destinations.PostEdit
import com.dpm.sixpack.presentation.routes.postedit.PostEditRoute

fun NavController.navigateToPostEdit(feedId: Long) {
    navigate(PostEdit(feedId = feedId))
}

fun NavGraphBuilder.addPostDetailNavGraph(navigateToBack: () -> Unit = {}) {
    composable<PostEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<PostEdit>()

        PostEditRoute(
            feedId = route.feedId,
            onNavigateBack = navigateToBack,
        )
    }
}
