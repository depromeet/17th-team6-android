package com.dpm.sixpack.presentation.routes.postdetail.navigation

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
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
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
) {
    val navigationAnimationSpec =
        tween<IntOffset>(
            durationMillis = 300,
            easing = LinearEasing,
        )
    val fadeAnimationSpec =
        tween<Float>(
            durationMillis = 300,
            easing = LinearEasing,
        )
    composable<PostDetail>(
        deepLinks =
            listOf(
                navDeepLink {
                    action = Intent.ACTION_VIEW
                    uriPattern = DeepLinks.Feed.DETAIL
                },
            ),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = navigationAnimationSpec,
            ) + fadeIn(fadeAnimationSpec)
        },
        exitTransition = {
            fadeOut(animationSpec = fadeAnimationSpec)
        },
        popEnterTransition = {
            fadeIn(animationSpec = fadeAnimationSpec)
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = navigationAnimationSpec,
            ) + fadeOut(fadeAnimationSpec)
        },
    ) { backStackEntry ->
        PostDetailRoute(
            navigateToMyPage = navigateToMyPage,
            navigateToBack = navigateToBack,
            navigateToUserProfile = navigateToUserProfile,
            navigateToPostEdit = navigateToPostEdit,
            onShowSnackBar = onShowSnackBar,
        )
    }
}
