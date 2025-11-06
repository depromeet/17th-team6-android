package com.dpm.sixpack.presentation.routes.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.mypage.MyPageRoute

fun NavController.navigateMyPage(navOptions: NavOptions? = null) {
    navigate(MainRoute.MyPage, navOptions)
}

fun NavGraphBuilder.addMyPageNavGraph(
    onNavigateToSettings: () -> Unit,
    onNavigateToPostDetail: (Long) -> Unit,
    onNavigateToRecordDetail: (Long) -> Unit,
) {
    composable<MainRoute.MyPage> {
        MyPageRoute(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToPostDetail = onNavigateToPostDetail,
            onNavigateToRecordDetail = onNavigateToRecordDetail,
        )
    }
}
