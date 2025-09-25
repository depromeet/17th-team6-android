package com.dpm.sixpack.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.dpm.sixpack.SixPackAppState
import com.dpm.sixpack.presentation.routes.home.navigation.addHomeNavGraph
import com.dpm.sixpack.presentation.routes.onboarding.navigation.addOnboardingNavGraph
import com.dpm.sixpack.presentation.routes.session.navigation.addRunningNavGraph
import com.dpm.sixpack.presentation.routes.session_list.navigation.addSessionListNavGraph
import timber.log.Timber

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
            addOnboardingNavGraph(
                navController = navigator.navController,
                onCompleteOnboarding = navigator::navigateToHome,
            )

            addHomeNavGraph(
                onNavigateToSessionList = navigator::navigateToSessionList,
                onNavigateToSession = {
                    Timber.d("SR-N onNavigateToSession")
                },
                onNavigateToGoalEdit = {
                    Timber.d("SR-N onNavigateToGoalEdit")
                },
            )

            addSessionListNavGraph {
                Timber.d("SL-N onNavigateToSession: $it")
            }

            addRunningNavGraph()
        }
    }
}
