package com.dpm.sixpack.presentation.routes.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SettingsAccountInfoRoute
import com.dpm.sixpack.presentation.destinations.SettingsProfileEditRoute
import com.dpm.sixpack.presentation.destinations.SettingsPushNotificationRoute
import com.dpm.sixpack.presentation.destinations.SettingsRoute
import com.dpm.sixpack.presentation.routes.settings.SettingsRoute
import com.dpm.sixpack.presentation.routes.settings.accountinfo.AccountInfoRoute
import com.dpm.sixpack.presentation.routes.settings.profileedit.ProfileEditRoute
import com.dpm.sixpack.presentation.routes.settings.pushnotification.PushNotificationRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SettingsRoute, navOptions)
}

fun NavController.navigateToSettingsProfileEdit(navOptions: NavOptions? = null) {
    navigate(SettingsProfileEditRoute, navOptions)
}

fun NavController.navigateToSettingsAccountInfo(navOptions: NavOptions? = null) {
    navigate(SettingsAccountInfoRoute, navOptions)
}

fun NavController.navigateToSettingsPushNotification(navOptions: NavOptions? = null) {
    navigate(SettingsPushNotificationRoute, navOptions)
}

fun NavGraphBuilder.addSettingsNavGraph(
    onNavigateBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navController: NavController,
) {
    composable<SettingsRoute> {
        SettingsRoute(
            onNavigateBack = onNavigateBack,
            onNavigateToProfileEdit = {
                navController.navigateToSettingsProfileEdit()
            },
            onNavigateToAccountInfo = {
                navController.navigateToSettingsAccountInfo()
            },
            onNavigateToPushNotification = {
                navController.navigateToSettingsPushNotification()
            },
            onShowLogoutDialog = {
                // TODO: 로그아웃 다이얼로그 표시
            },
            onShowWithdrawDialog = {
                // TODO: 탈퇴 다이얼로그 표시
            },
        )
    }

    composable<SettingsProfileEditRoute> {
        ProfileEditRoute(
            onNavigateBack = onNavigateBack,
            onShowSnackbar = onShowSnackbar,
        )
    }

    composable<SettingsAccountInfoRoute> {
        AccountInfoRoute(
            onNavigateBack = onNavigateBack,
        )
    }

    composable<SettingsPushNotificationRoute> {
        PushNotificationRoute(
            onNavigateBack = onNavigateBack,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
