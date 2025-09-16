package com.dpm.sixpack.presentation.navigation

import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.MainRoute

enum class MainNavTab(
    val iconResId: Int,
    internal val descriptionResId: Int,
    val route: MainRoute,
) {
    HOME(
        iconResId = R.drawable.ic_home_nav,
        descriptionResId = R.string.nav_tab_home_description,
        route = MainRoute.Home,
    ),
    SESSION(
        iconResId = R.drawable.ic_running_nav,
        descriptionResId = R.string.nav_tab_session_description,
        route = MainRoute.Session,
    ),
    RECORD(
        iconResId = R.drawable.ic_record_nav,
        descriptionResId = R.string.nav_tab_record_description,
        route = MainRoute.Record,
    ),
    MY_PAGE(
        iconResId = R.drawable.ic_mypage_nav,
        descriptionResId = R.string.nav_tab_mypage_description,
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
