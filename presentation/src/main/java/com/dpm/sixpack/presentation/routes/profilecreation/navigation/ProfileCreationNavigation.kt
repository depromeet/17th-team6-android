package com.dpm.sixpack.presentation.routes.profilecreation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.destinations.ProfileCreationRoute
import com.dpm.sixpack.presentation.routes.profilecreation.ProfileCreationRoute

fun NavController.navigateProfileCreation(
    phoneNumber: String,
    navOptions: NavOptions? = null,
) {
    navigate(ProfileCreationRoute(phoneNumber = phoneNumber), navOptions)
}

fun NavGraphBuilder.addProfileCreationNavGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToBack: () -> Unit,
) {
    composable<ProfileCreationRoute> { backStackEntry ->
        ProfileCreationRoute(
            onNavigateToHome = onNavigateToHome,
            onNavigateBack = onNavigateToBack,
        )
    }
}
