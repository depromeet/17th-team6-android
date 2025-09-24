package com.dpm.sixpack.presentation.routes.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.onboarding.permission.OnboardingPermissionRoute


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
            OnboardingPermissionRoute(
                navigateToNext = {
                    navController.navigate(OnboardingRoute.LevelSelection)
                },
                navigateToBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<OnboardingRoute.LevelSelection> {

        }

        composable<OnboardingRoute.GoalSelection> {
        }

        composable<OnboardingRoute.GoalTarget> {
        
        }
    }
}
