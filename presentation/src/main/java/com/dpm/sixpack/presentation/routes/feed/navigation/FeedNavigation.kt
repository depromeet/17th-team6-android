package com.dpm.sixpack.presentation.routes.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.feed.FeedRoute

fun NavController.navigateToFeed() {
    this.navigate(MainRoute.Feed)
}

fun NavGraphBuilder.addFeedScreen(onNavigateToBack: () -> Unit) {
    composable<MainRoute.Feed> {
        FeedRoute(onNavigateToBack = onNavigateToBack)
    }
}
