package com.dpm.sixpack.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.BuildConfig
import com.dpm.sixpack.LocalTimeZone
import com.dpm.sixpack.core.util.NetworkMonitor
import com.dpm.sixpack.core.util.TimeZoneMonitor
import com.dpm.sixpack.main.navigation.rememberMainNavigator
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
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

        // 다크모드 여부에 따라 상태바 아이콘 색상 설정
        setStatusBarIconColor()

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

                // AuthEvent 처리: 토큰 만료/갱신 실패 시 Onboarding 화면으로 이동
                LaunchedEffect(Unit) {
                    viewModel.container.sideEffectFlow.collect { sideEffect ->
                        when (sideEffect) {
                            is MainSideEffect.NavigateToOnboarding -> {
                                // Onboarding 화면으로 이동 (백스택 전체 클리어)
                                appState.navigator.navController.navigate(OnboardingRoute) {
                                    // 백스택 전체 클리어
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }

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

    /**
     * 상태바 아이콘 색상을 어두운 색으로 고정
     * 현재 앱이 다크모드를 지원하지 않으므로 (항상 밝은 배경), 상태바 아이콘도 항상 어두운 색으로 설정
     */
    private fun setStatusBarIconColor() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            // isAppearanceLightStatusBars = true -> 어두운 아이콘 (검은색)
            isAppearanceLightStatusBars = true
        }
    }
}
