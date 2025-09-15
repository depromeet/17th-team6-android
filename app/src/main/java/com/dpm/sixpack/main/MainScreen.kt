package com.dpm.sixpack.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.main.navigation.MainNavHost
import com.dpm.sixpack.presentation.navigation.MainBottomBar
import com.dpm.sixpack.presentation.navigation.MainNavTab

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    appState: SixPackAppState,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    // FIXME: Replace with actual string resource
    val notConnectedMessage = "stringResource(R.string.not_connected)"

    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = Indefinite,
            )
        }
    }

    MainScreenContent(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        appState = appState,
    )
}

@Composable
internal fun MainScreenContent(
    appState: SixPackAppState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            MainBottomBar(
                modifier = Modifier.navigationBarsPadding(),
                visible = appState.navigator.shouldShowBottomBar(),
                mainNavTabs = MainNavTab.entries,
                currentTab = appState.navigator.currentTab,
                onTabSelected = { tab ->
                    appState.navigator.navigate(tab)
                }
            )
        }
    ) { paddingValue ->
        MainNavHost(
            appState = appState,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == ActionPerformed
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValue.calculateBottomPadding()),
        )
    }
}
