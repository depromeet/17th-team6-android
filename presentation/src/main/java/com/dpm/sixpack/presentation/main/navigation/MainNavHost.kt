package com.dpm.sixpack.presentation.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination
    ) {
        // NavGraph Functions
    }
}
