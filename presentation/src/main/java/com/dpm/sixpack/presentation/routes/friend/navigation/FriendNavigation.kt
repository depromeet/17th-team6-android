package com.dpm.sixpack.presentation.routes.friend.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dpm.sixpack.presentation.destinations.Friend
import com.dpm.sixpack.presentation.destinations.FriendRoute
import com.dpm.sixpack.presentation.routes.friend.AddFriendRoute
import com.dpm.sixpack.presentation.routes.friend.FriendListRoute
import com.dpm.sixpack.presentation.routes.friend.FriendViewModel

fun NavController.navigateToFriendGraph(navOptions: NavOptions? = null) {
    navigate(Friend, navOptions)
}

fun NavGraphBuilder.addFriendNavGraph(
    navController: NavHostController,
    navigateToBack: () -> Unit,
    onShowSnackBar: (String, String?) -> Unit,
) {
    navigation<Friend>(
        startDestination = FriendRoute.FriendListRoute,
    ) {
        composable<FriendRoute.FriendListRoute> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<Friend>()
                }
            val sharedViewModel: FriendViewModel = hiltViewModel(parentEntry)

            FriendListRoute(
                viewModel = sharedViewModel,
                navigateToBack = navigateToBack,
                navigateToAddFriend = {
                    navController.navigate(FriendRoute.AddFriendRoute)
                },
                onShowSnackBar = onShowSnackBar,
            )
        }

        composable<FriendRoute.AddFriendRoute> { backStackEntry ->
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
