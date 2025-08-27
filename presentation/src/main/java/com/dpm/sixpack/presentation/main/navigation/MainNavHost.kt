package com.dpm.sixpack.presentation.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.dpm.sixpack.presentation.example.navigation.sampleNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
        ) {
            sampleNavGraph(
                onNextClick = { },
                onBackClick = { navigator.popBackStack() },
            )
        }
    }
}
