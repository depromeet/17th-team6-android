package com.dpm.sixpack.presentation.routes.signin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SignInRoute
import com.dpm.sixpack.presentation.routes.signin.SignInRoute

fun NavController.navigateSignIn(navOptions: NavOptions? = null) {
    navigate(SignInRoute, navOptions)
}

fun NavGraphBuilder.addSignInNavGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: (phoneNumber: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<SignInRoute> {
        SignInRoute(
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignUp = onNavigateToSignUp,
            onNavigateBack = onNavigateBack,
        )
    }
}
