package com.dpm.sixpack.presentation.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dpm.sixpack.presentation.main.navigation.MainNavHost
import com.dpm.sixpack.presentation.main.navigation.MainNavigator
import com.dpm.sixpack.presentation.main.navigation.rememberMainNavigator

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    navigator: MainNavigator = rememberMainNavigator(),
) {
    MainScreenContent(
        modifier = modifier,
        navigator = navigator,
    )
}

@Composable
internal fun MainScreenContent(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
) {
    Scaffold(
        modifier = modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        topBar = { TopAppBar(title = { Text("Sample Main Screen") }) },
    ) { paddingValue ->
        MainNavHost(
            navigator = navigator,
            modifier = Modifier.padding(paddingValue),
        )
    }
}
