package com.dpm.sixpack.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.dpm.sixpack.data.util.NetworkMonitor
import com.dpm.sixpack.data.util.TimeZoneMonitor
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appState = rememberSixPackAppState(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
                navController = rememberNavController(),
            )
            SixpackTheme {
                MainScreen(appState = appState)
            }
        }
    }
}
