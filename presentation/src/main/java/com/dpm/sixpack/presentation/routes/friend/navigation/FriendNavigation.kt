package com.dpm.sixpack.presentation.routes.friend.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.common.util.constant.DeepLinks
import com.dpm.sixpack.presentation.destinations.AddFriendRoute
import com.dpm.sixpack.presentation.destinations.Friend
import com.dpm.sixpack.presentation.destinations.FriendListRoute
import com.dpm.sixpack.presentation.destinations.FriendProfile
import com.dpm.sixpack.presentation.routes.friend.AddFriendRoute
import com.dpm.sixpack.presentation.routes.friend.FriendListRoute
import com.dpm.sixpack.presentation.routes.friend.FriendViewModel
import timber.log.Timber
import com.dpm.sixpack.presentation.routes.friendprofile.FriendProfileRoute as FriendProfileRouteComposable

fun NavController.navigateToFriendGraph(navOptions: NavOptions? = null) {
    navigate(Friend, navOptions)
}

fun NavController.navigateToFriendProfile(
    friendId: Long,
    navOptions: NavOptions? = null,
) {
    navigate(FriendProfile(friendId = friendId), navOptions)
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
fun NavGraphBuilder.addFriendProfileNavGraph(
    navigateToBack: () -> Unit = {},
    navigateToPostDetail: (Long) -> Unit = {},
) {
    composable<FriendProfile>(
        deepLinks =
            listOf(
                navDeepLink<FriendProfile>(basePath = DeepLinks.Friend.PROFILE),
            ),
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<FriendProfile>()
        Timber.d("SR-N 아이디는? ${route.friendId}")
        FriendProfileRouteComposable(
            onNavigateBack = navigateToBack,
            onNavigateToPostDetail = navigateToPostDetail,
        )
    }
}
