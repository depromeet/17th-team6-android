package com.dpm.sixpack.presentation.routes.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.routes.Route
import com.dpm.sixpack.presentation.routes.map.MapRoute

fun NavController.navigateMap(navOptions: NavOptions? = null) {
    navigate(Route.Map, navOptions)
}

fun NavGraphBuilder.addMapNavGraph() {
    composable<Route.Map> {
        MapRoute(
            // fill
        )
    }
}
