package com.dpm.sixpack.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.dpm.sixpack.presentation.navigation.MainNavTab
import com.dpm.sixpack.presentation.routes.MainRoute
import com.dpm.sixpack.presentation.routes.Route
import com.dpm.sixpack.presentation.routes.map.navigation.navigateMap
import com.dpm.sixpack.presentation.routes.session.navigation.navigateRunningSession

class MainNavigator(
    val navController: NavHostController,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
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
        @Composable get() = MainNavTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    // FIXME: Change to other when the start screen is implemented
    val startDestination = MainRoute.Session

    fun navigateMap() {
        navController.navigateMap(
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigate(tab: MainNavTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainNavTab.HOME -> {}
            MainNavTab.SESSION -> navController.navigateRunningSession(navOptions)
            MainNavTab.RECORD -> {}
            MainNavTab.MY_PAGE -> {}
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean =
        navController.currentDestination?.hasRoute<T>() == true

    @Composable
    fun shouldShowBottomBar() = MainNavTab.contains {
        currentDestination?.hasRoute(it::class) == true
    }
}

@Composable
internal fun rememberMainNavigator(navController: NavHostController = rememberNavController()): MainNavigator =
    remember(navController) {
        MainNavigator(navController)
    }
