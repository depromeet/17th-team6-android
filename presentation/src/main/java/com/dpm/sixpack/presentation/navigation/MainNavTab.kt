package com.dpm.sixpack.presentation.navigation

import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.MainRoute

enum class MainNavTab(
    val iconResId: Int,
    internal val contentDescription: String,
    val route: MainRoute,
) {
    HOME(
        iconResId = R.drawable.ic_home_nav,
        contentDescription = "홈",
        route = MainRoute.Home,
    ),
    SESSION(
        iconResId = R.drawable.ic_running_nav,
        contentDescription = "러닝",
        route = MainRoute.Session,
    ),
    RECORD(
        iconResId = R.drawable.ic_record_nav,
        contentDescription = "기록",
        route = MainRoute.Record,
    ),
    MY_PAGE(
        iconResId = R.drawable.ic_mypage_nav,
        contentDescription = "내 정보",
        route = MainRoute.MyPage,
    ),
    ;

    companion object {
        @Composable
        fun find(predicate: @Composable (MainRoute) -> Boolean): MainNavTab? =
            entries.find { predicate(it.route) }

        @Composable
        fun contains(predicate: @Composable (MainRoute) -> Boolean): Boolean =
            entries.map { it.route }.any { predicate(it) }
    }
}
