package com.dpm.sixpack.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
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
import timber.log.Timber
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

        handleNotificationData(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // ⚠️ 앱이 이미 켜져있는데(백그라운드 상태) 알림을 클릭한 경우
        handleNotificationData(intent)
    }

    private fun handleNotificationData(intent: Intent?) {
        // 1. Intent의 extras에서 data 페이로드의 키("deeplink")를 확인
        val deeplink = intent?.extras?.getString("deeplink")

        if (deeplink != null) {
            Timber.d("Received deeplink from notification: $deeplink")

            // 2. 딥링크 Uri로 이동 (NavHostController 또는 Intent 처리)
            val uri = deeplink.toUri()

            // TODO: 딥링크 URI를 처리하는 실제 내비게이션 로직 구현
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
