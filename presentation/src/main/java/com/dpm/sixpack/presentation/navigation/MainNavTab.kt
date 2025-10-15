package com.dpm.sixpack.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.destinations.MainRoute

enum class MainNavTab(
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int,
    val route: MainRoute,
) {
    RUNNING(
        iconResId = R.drawable.ic_running_nav,
        titleResId = R.string.nav_tab_running_title,
        route = MainRoute.Home,
    ),
    FEED(
        iconResId = R.drawable.ic_feed,
        titleResId = R.string.nav_tab_feed_title,
        route = MainRoute.Feed,
    ),
    MY_PAGE(
        iconResId = R.drawable.ic_mypage_nav,
        titleResId = R.string.nav_tab_mypage_title,
        route = MainRoute.MyPage,
    ),
    ;

    companion object {
        @Composable
        fun find(predicate: @Composable (MainRoute) -> Boolean): MainNavTab? = entries.find { predicate(it.route) }

        @Composable
        fun contains(predicate: @Composable (MainRoute) -> Boolean): Boolean =
            entries.map { it.route }.any { predicate(it) }
    }
}
