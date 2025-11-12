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
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.Route
import com.dpm.sixpack.presentation.navigation.MainNavTab
import com.dpm.sixpack.presentation.routes.feed.navigation.navigateToFeed
import com.dpm.sixpack.presentation.routes.mypage.navigation.navigateMyPage
import com.dpm.sixpack.presentation.routes.report.navigation.navigateSessionReport
import com.dpm.sixpack.presentation.routes.running.navigation.navigateRunning

class MainNavigator(
    val navController: NavHostController,
    val startDestination: Route,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)
    private var bottomBarVisibility = mutableStateOf(true)

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

    fun navigateToHome() {
        val navOptions =
            navOptions {
                // 그래프의 시작점을 찾아서 그곳까지 팝합니다.
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true // startDestination을 포함한 스택 전체를 제거
                }
                // 홈 화면이 스택의 유일한 인스턴스가 되도록 보장합니다.
                launchSingleTop = true
            }

        navController.navigate(startDestination, navOptions)
    }

    fun navigateToSessionReport(sessionId: Long) {
        navController.navigateSessionReport(
            sessionId,
            navOptions {
                popUpTo(MainRoute.Running) {
                    inclusive = false
                }
            },
        )
    }

    fun navigateToFeed() {
        navController.navigateToFeed(
            navOptions {
                popUpTo(
                    navController.graph.findStartDestination().id,
                ) {
                    inclusive = false
                }
                launchSingleTop = true
                restoreState = true
            },
        )
    }

    fun navigateToMyPage() {
        navController.navigateMyPage(
            navOptions {
                popUpTo(
                    navController.graph.findStartDestination().id,
                ) {
                    inclusive = false
                }
                launchSingleTop = true
                restoreState = true
            },
        )
    }

    fun navigate(tab: MainNavTab) {
        // 이미 같은 탭에 있으면 네비게이션 실행하지 않음 (화면 재구성 방지)
        if (navController.currentDestination?.hasRoute(tab.route::class) == true) {
            return
        }

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
                navController.navigateRunning(navOptions)
            }

            MainNavTab.FEED -> {
                navController.navigateToFeed(navOptions)
            }

            MainNavTab.MY_PAGE -> {
                navController.navigateMyPage(navOptions)
            }
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean =
        navController.currentDestination?.hasRoute<T>() == true

    @Composable
    fun shouldShowBottomBar() =
        bottomBarVisibility.value &&
            MainNavTab.contains {
                currentDestination?.hasRoute(it::class) == true
            }

    fun setBottomBarVisibility(isVisible: Boolean) {
        bottomBarVisibility.value = isVisible
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
