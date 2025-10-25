package com.dpm.sixpack.presentation.routes.profilecreation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.ProfileCreationRoute
import com.dpm.sixpack.presentation.routes.profilecreation.ProfileCreationRoute

fun NavController.navigateProfileCreation(navOptions: NavOptions? = null) {
    navigate(ProfileCreationRoute, navOptions)
}

fun NavGraphBuilder.addProfileCreationNavGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToBack: () -> Unit,
) {
    composable<ProfileCreationRoute> {
        ProfileCreationRoute(
            onNavigateToHome = onNavigateToHome,
            onNavigateBack = onNavigateToBack,
        )
    }
}
