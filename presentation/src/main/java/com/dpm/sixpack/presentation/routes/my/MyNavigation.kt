package com.dpm.sixpack.presentation.routes.my

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.running.RunningRoute

fun NavController.navigateMyPage(navOptions: NavOptions? = null) {
    navigate(MainRoute.MyPage, navOptions)
}

fun NavGraphBuilder.addMyPageNavGraph() {
    composable<MainRoute.MyPage> {
        SettingsScreen()
    }
}
