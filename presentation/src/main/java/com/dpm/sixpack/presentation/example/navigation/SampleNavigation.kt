package com.dpm.sixpack.presentation.example.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.example.ExampleScreen
import com.dpm.sixpack.presentation.routes.Route

fun NavController.navigateSample(navOptions: NavOptions? = null) {
    navigate(Route.Example, navOptions)
}

fun NavGraphBuilder.sampleNavGraph(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable<Route.Example> {
        ExampleScreen(
            onNextClick = onNextClick,
            onBackClick = onBackClick,
        )
    }
}
