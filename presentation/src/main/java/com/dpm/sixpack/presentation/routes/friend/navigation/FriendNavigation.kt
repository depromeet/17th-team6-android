package com.dpm.sixpack.presentation.routes.friend.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
import com.dpm.sixpack.presentation.destinations.AddFriendRoute
import com.dpm.sixpack.presentation.destinations.Friend
import com.dpm.sixpack.presentation.destinations.FriendListRoute
import com.dpm.sixpack.presentation.destinations.FriendProfile
import com.dpm.sixpack.presentation.routes.friend.AddFriendRoute
import com.dpm.sixpack.presentation.routes.friend.FriendListRoute
import com.dpm.sixpack.presentation.routes.friend.FriendViewModel
import com.dpm.sixpack.presentation.theme.SixpackTheme

fun NavController.navigateToFriendGraph(navOptions: NavOptions? = null) {
    navigate(Friend, navOptions)
}

fun NavGraphBuilder.addFriendNavGraph(
    navController: NavHostController,
    navigateToBack: () -> Unit,
    onShowSnackBar: (String, String?) -> Unit,
) {
    navigation<Friend>(
        startDestination = FriendListRoute,
    ) {
        composable<FriendListRoute> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<Friend>()
                }
            val sharedViewModel: FriendViewModel = hiltViewModel(parentEntry)

            FriendListRoute(
                viewModel = sharedViewModel,
                navigateToBack = navigateToBack,
                navigateToAddFriend = {
                    navController.navigate(AddFriendRoute)
                },
                onShowSnackBar = onShowSnackBar,
            )
        }

        composable<AddFriendRoute>(
            deepLinks =
                listOf(
                    navDeepLink<AddFriendRoute>(basePath = DeepLinks.Friend.ADD),
                ),
        ) { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<Friend>()
                }
            val sharedViewModel: FriendViewModel = hiltViewModel(parentEntry)

            AddFriendRoute(
                viewModel = sharedViewModel,
                navigateToBack = { navController.popBackStack() },
                onShowSnackBar = onShowSnackBar,
            )
        }
    }
}

/**
 * 친구 프로필 NavGraph 추가
 */
fun NavGraphBuilder.addFriendProfileNavGraph(navigateToBack: () -> Unit = {}) {
    composable<FriendProfile>(
        deepLinks =
            listOf(
                navDeepLink<FriendProfile>(basePath = DeepLinks.Friend.PROFILE),
            ),
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<FriendProfile>()
        FriendProfileScreen(
            friendId = route.friendId,
            navigateToBack = navigateToBack,
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
                        color = SixpackTheme.colors.gray900,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "친구 프로필 화면 (Friend ID: $friendId)\n\n구현 예정",
                style = SixpackTheme.typography.b1Regular,
                color = SixpackTheme.colors.gray700,
            )
        }
    }
}
