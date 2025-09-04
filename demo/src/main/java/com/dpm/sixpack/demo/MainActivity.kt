package com.dpm.sixpack.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.data.util.NetworkMonitor
import com.dpm.sixpack.data.util.TimeZoneMonitor
import com.dpm.sixpack.demo.navigation.rememberMainNavigator
import com.dpm.sixpack.presentation.theme.SixpackTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appState =
                rememberSixPackDemoAppState(
                    navigator = rememberMainNavigator(),
                    networkMonitor = networkMonitor,
                    timeZoneMonitor = timeZoneMonitor,
                )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalTimeZone provides currentTimeZone,
            ) {
                SixpackTheme {
                    MainScreen(appState = appState)
                }
            }
        }
    }
}
