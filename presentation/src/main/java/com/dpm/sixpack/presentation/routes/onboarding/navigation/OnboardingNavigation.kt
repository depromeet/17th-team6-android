package com.dpm.sixpack.presentation.routes.onboarding.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingViewModel
import com.dpm.sixpack.presentation.routes.onboarding.routes.level.OnboardingLevelRoute
import com.dpm.sixpack.presentation.routes.onboarding.routes.permission.OnboardingPermissionRoute

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
            val backStackEntry = navController.getBackStackEntry(OnboardingRoute.Onboarding)
            val viewModel: OnboardingViewModel = hiltViewModel(backStackEntry)

            OnboardingPermissionRoute(
                viewModel = viewModel,
                navigateToLevel = {
                    navController.navigate(OnboardingRoute.LevelSelection)
                },
                navigateToBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<OnboardingRoute.LevelSelection> {
            val backStackEntry = navController.getBackStackEntry(OnboardingRoute.Onboarding)
            val viewModel: OnboardingViewModel = hiltViewModel(backStackEntry)

            OnboardingLevelRoute(
                viewModel = viewModel,
                navigateToGoal = {
                    navController.navigate(OnboardingRoute.GoalSelection)
                },
                navigateToBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<OnboardingRoute.GoalSelection> {
        }

        composable<OnboardingRoute.GoalTarget> {
        }
    }
}
