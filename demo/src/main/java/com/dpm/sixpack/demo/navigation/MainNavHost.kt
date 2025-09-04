package com.dpm.sixpack.demo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.dpm.sixpack.demo.SixPackAppState
import com.dpm.sixpack.presentation.map.navigation.addMapNavGraph

@Composable
internal fun MainNavHost(
    appState: SixPackAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navigator = appState.navigator

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
        ) {
            addMapNavGraph()
        }
    }
}
