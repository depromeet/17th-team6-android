package com.dpm.sixpack.presentation.routes.deprecated.onboarding.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.OnboardingFinishRoute
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.OnboardingGoalRoute
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.OnboardingLevelRoute
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.OnboardingPermissionRoute
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.OnboardingViewModel

fun NavController.navigateOnboarding(navOptions: NavOptions? = null) {
    navigate(OnboardingRoute.Onboarding, navOptions)
}

fun NavGraphBuilder.addOnboardingNavGraph(
    navController: NavHostController,
    navigateToHome: () -> Unit,
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
            val viewModel: OnboardingViewModel =
                if (navController.currentDestination?.parent?.route ==
                    OnboardingRoute.Onboarding
                        .serializer()
                        .descriptor.serialName
                ) {
                    hiltViewModel(navController.getBackStackEntry(OnboardingRoute.Onboarding))
                } else {
                    hiltViewModel()
                }

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
            val viewModel: OnboardingViewModel =
                if (navController.currentDestination?.parent?.route ==
                    OnboardingRoute.Onboarding
                        .serializer()
                        .descriptor.serialName
                ) {
                    hiltViewModel(navController.getBackStackEntry(OnboardingRoute.Onboarding))
                } else {
                    hiltViewModel()
                }

            OnboardingGoalRoute(
                viewModel = viewModel,
                navigateToFinish = {
                    navController.navigate(OnboardingRoute.Finish)
                },
                navigateToBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<OnboardingRoute.Finish> {
            val viewModel: OnboardingViewModel =
                if (navController.currentDestination?.parent?.route ==
                    OnboardingRoute.Onboarding
                        .serializer()
                        .descriptor.serialName
                ) {
                    hiltViewModel(navController.getBackStackEntry(OnboardingRoute.Onboarding))
                } else {
                    hiltViewModel()
                }

            OnboardingFinishRoute(
                viewModel = viewModel,
                navigateToHome = {
                    navigateToHome()
                },
                navigateToBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
