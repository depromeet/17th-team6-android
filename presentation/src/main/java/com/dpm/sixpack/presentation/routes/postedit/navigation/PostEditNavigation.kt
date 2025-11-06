package com.dpm.sixpack.presentation.routes.postedit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.PostEdit
import com.dpm.sixpack.presentation.routes.postedit.PostEditRoute

fun NavController.navigateToPostEdit(feedId: Long) {
    navigate(PostEdit(feedId = feedId))
}

fun NavGraphBuilder.addPostEditNavGraph(navigateBack: () -> Unit = {}) {
    composable<PostEdit> {
        PostEditRoute(
            navigateBack = navigateBack,
        )
    }
}
