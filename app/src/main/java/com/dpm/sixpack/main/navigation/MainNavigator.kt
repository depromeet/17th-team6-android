package com.dpm.sixpack.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.Route
import com.dpm.sixpack.presentation.navigation.MainNavTab
import com.dpm.sixpack.presentation.routes.session.navigation.navigateRunningSession
import com.dpm.sixpack.presentation.routes.sessionreport.navigation.navigateSessionReport
import com.dpm.sixpack.presentation.routes.signup.navigation.navigateSignUp
import timber.log.Timber

class MainNavigator(
    val navController: NavHostController,
    val startDestination: Route,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry =
                navController.currentBackStackEntryFlow
                    .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val currentTab: MainNavTab?
        @Composable get() =
            MainNavTab.find { tab ->
                currentDestination?.hasRoute(tab::class) == true
            }

    // FIXME: Change to other when the start screen is implemented

    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigateToHome(navOptions: NavOptions? = null) {
    }

    fun navigateToSignUp() {
        navController.navigateSignUp()
    }

    fun navigateToRunningSession() {
        navController.navigateRunningSession()
    }

    fun navigateToSessionReport() {
        navController.navigateSessionReport(
            navOptions {
                popUpTo(MainRoute.Home) {
                    inclusive = false
                }
            },
        )
    }

    fun navigate(tab: MainNavTab) {
        val navOptions =
            navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

        when (tab) {
            MainNavTab.RUNNING -> {
                // TODO
            }

            MainNavTab.FEED -> {
                // TODO
                Timber.d("Navigate to Record Screen")
            }

            MainNavTab.MY_PAGE -> {
                // TODO
                Timber.d("Navigate to MyPage Screen")
            }
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean =
        navController.currentDestination?.hasRoute<T>() == true

    @Composable
    fun shouldShowBottomBar() =
        MainNavTab.contains {
            currentDestination?.hasRoute(it::class) == true
        }
}

@Composable
internal fun rememberMainNavigator(
    startDestination: Route,
    navController: NavHostController = rememberNavController(),
): MainNavigator =
    remember(navController, startDestination) {
        MainNavigator(navController, startDestination)
    }
