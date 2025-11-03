package com.dpm.sixpack.presentation.routes.terms.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.TermsRoute
import com.dpm.sixpack.presentation.routes.terms.TermsRoute

fun NavController.navigateTerms(navOptions: NavOptions? = null) {
    navigate(TermsRoute, navOptions)
}

fun NavGraphBuilder.addTermsNavGraph(
    onNavigateToSignUp: () -> Unit,
    onNavigateToBack: () -> Unit,
) {
    composable<TermsRoute> {
        TermsRoute(
            onNavigateToSignUp = onNavigateToSignUp,
            onNavigateBack = onNavigateToBack,
        )
    }
}
