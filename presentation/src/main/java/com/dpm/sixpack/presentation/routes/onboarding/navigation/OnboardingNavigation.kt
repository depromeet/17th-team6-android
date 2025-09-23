package com.dpm.sixpack.presentation.routes.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dpm.sixpack.presentation.destinations.OnboardingRoute


fun NavController.navigateOnboarding(navOptions: NavOptions? = null) {
    navigate(OnboardingRoute.Onboarding, navOptions)
}

fun NavGraphBuilder.addOnboardingNavGraph(
    navController: NavHostController,
    onCompleteOnboarding: () -> Unit,
) {
    navigation<OnboardingRoute.Onboarding>(
        startDestination = OnboardingRoute.Permission,
    ) {
        composable<OnboardingRoute.Permission> {

        }

        composable<OnboardingRoute.LevelSelection> {

        }

        composable<OnboardingRoute.GoalSelection> {
        }

        composable<OnboardingRoute.GoalTarget> {
        
        }
    }
}
