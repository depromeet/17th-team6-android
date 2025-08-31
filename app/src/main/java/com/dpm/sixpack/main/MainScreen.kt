package com.dpm.sixpack.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.main.navigation.MainNavHost
import com.dpm.sixpack.main.navigation.MainNavigator

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    appState: SixPackAppState,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val notConnectedMessage = "stringResource(R.string.not_connected)" // FIXME

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
        modifier = modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        topBar = { TopAppBar(title = { Text("Sample Main Screen") }) },
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
            modifier = Modifier.padding(paddingValue),
        )
    }
}
