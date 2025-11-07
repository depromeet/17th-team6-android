package com.dpm.sixpack.presentation.routes.friend.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
import com.dpm.sixpack.presentation.destinations.FriendAdd
import com.dpm.sixpack.presentation.destinations.FriendProfile
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 친구 프로필 화면으로 이동
 * @param friendId 친구 ID
 */
fun NavController.navigateToFriendProfile(friendId: Long) {
    navigate(FriendProfile(friendId = friendId))
}

/**
 * 친구 추가 화면으로 이동
 */
fun NavController.navigateToFriendAdd() {
    navigate(FriendAdd)
}

/**
 * 친구 프로필 NavGraph 추가
 */
fun NavGraphBuilder.addFriendProfileNavGraph(
    navigateToBack: () -> Unit = {},
) {
    composable<FriendProfile>(
        deepLinks = listOf(
            navDeepLink<FriendProfile>(basePath = DeepLinks.Friend.PROFILE)
        )
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<FriendProfile>()
        FriendProfileScreen(
            friendId = route.friendId,
            navigateToBack = navigateToBack
        )
    }
}

/**
 * 친구 추가 NavGraph 추가
 */
fun NavGraphBuilder.addFriendAddNavGraph(
    navigateToBack: () -> Unit = {},
) {
    composable<FriendAdd>(
        deepLinks = listOf(
            navDeepLink<FriendAdd>(basePath = DeepLinks.Friend.ADD)
        )
    ) {
        FriendAddScreen(
            navigateToBack = navigateToBack
        )
    }
}

// 임시 플레이스홀더 화면들

/**
 * 친구 프로필 화면 (임시 구현)
 * TODO: 실제 구현 필요
 */
@Composable
private fun FriendProfileScreen(
    friendId: Long,
    navigateToBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = navigateToBack,
                titleContent = {
                    Text(
                        text = "친구 프로필",
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "친구 프로필 화면 (Friend ID: $friendId)\n\n구현 예정",
                style = SixpackTheme.typography.b1Regular,
                color = SixpackTheme.colors.gray700
            )
        }
    }
}

/**
 * 친구 추가 화면 (임시 구현)
 * TODO: 실제 구현 필요
 */
@Composable
private fun FriendAddScreen(
    navigateToBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = navigateToBack,
                titleContent = {
                    Text(
                        text = "친구 추가",
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "친구 추가 화면\n\n구현 예정",
                style = SixpackTheme.typography.b1Regular,
                color = SixpackTheme.colors.gray700
            )
        }
    }
}
