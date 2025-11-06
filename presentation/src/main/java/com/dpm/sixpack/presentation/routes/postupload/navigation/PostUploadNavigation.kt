package com.dpm.sixpack.presentation.routes.postupload.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.destinations.PostUpload
import com.dpm.sixpack.presentation.routes.postedit.PostEditRoute
import com.dpm.sixpack.presentation.routes.postupload.PostUploadRoute

fun NavController.navigateToPostUpload(sessionId: Long, mapImgUrl: String, runningSummary: RunningSummary) {
    navigate(PostUpload(sessionId = sessionId, mapImageUrl = mapImgUrl, runningSummary = runningSummary))
}

fun NavGraphBuilder.addPostUploadNavGraph(
    navigateBack: () -> Unit = {},
    navigateToFeed: () -> Unit = {}
) {
    composable<PostUpload> {
        PostUploadRoute(
            navigateBack = navigateBack,
            navigateToFeed = navigateToFeed
        )
    }
}
