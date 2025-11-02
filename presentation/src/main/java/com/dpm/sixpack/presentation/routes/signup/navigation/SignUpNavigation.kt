package com.dpm.sixpack.presentation.routes.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SignUpRoute
import com.dpm.sixpack.presentation.routes.signup.SignUpRoute

fun NavController.navigateSignUp(navOptions: NavOptions? = null) {
    navigate(SignUpRoute, navOptions)
}

fun NavGraphBuilder.addSignUpNavGraph(
    onNavigateToProfileCreation: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<SignUpRoute> {
        SignUpRoute(
            onNavigateToProfileCreation = onNavigateToProfileCreation,
            onNavigateBack = onNavigateBack,
        )
    }
}
