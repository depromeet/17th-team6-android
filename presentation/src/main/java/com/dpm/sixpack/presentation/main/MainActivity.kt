package com.dpm.sixpack.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dpm.sixpack.presentation.main.navigation.rememberMainNavigator
import com.dpm.sixpack.presentation.theme.SixpackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigator = rememberMainNavigator()
            SixpackTheme {
                MainScreen(navigator = navigator)
            }
        }
    }
}
