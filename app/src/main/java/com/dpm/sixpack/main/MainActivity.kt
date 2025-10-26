package com.dpm.sixpack.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.BuildConfig
import com.dpm.sixpack.LocalTimeZone
import com.dpm.sixpack.core.util.NetworkMonitor
import com.dpm.sixpack.core.util.TimeZoneMonitor
import com.dpm.sixpack.main.navigation.rememberMainNavigator
import com.dpm.sixpack.presentation.theme.SixpackTheme
import com.dpm.sixpack.rememberSixPackAppState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        enableEdgeToEdge()

        setContent {
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val showFullScreenLoading by viewModel.showFullScreenLoading.collectAsStateWithLifecycle()

            if (!isLoading) {
                val appState =
                    rememberSixPackAppState(
                        navigator = rememberMainNavigator(startDestination = startDestination),
                        networkMonitor = networkMonitor,
                        timeZoneMonitor = timeZoneMonitor,
                    )

                val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

                CompositionLocalProvider(
                    LocalTimeZone provides currentTimeZone,
                ) {
                    SixpackTheme(isDebug = BuildConfig.DEBUG) {
                        MainScreen(
                            appState = appState,
                            showFullScreenLoading = showFullScreenLoading,
                            setFullScreenLoading = { viewModel.setFullScreenLoading(it) },
                        )
                    }
                }
            }
        }
    }
}
