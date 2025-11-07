package com.dpm.sixpack.presentation.routes.settings.pushnotification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.settings.pushnotification.contract.PushNotificationSideEffect
import com.dpm.sixpack.presentation.routes.settings.pushnotification.ui.screen.PushNotificationScreen

@Composable
fun PushNotificationRoute(
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PushNotificationViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsState()

    val marketingEnabledMessage = stringResource(R.string.settings_push_marketing_enabled_snackbar)
    val marketingDisabledMessage = stringResource(R.string.settings_push_marketing_disabled_snackbar)
    val notificationEnabledMessage = stringResource(R.string.settings_push_notification_enabled_snackbar)
    val notificationDisabledMessage = stringResource(R.string.settings_push_notification_disabled_snackbar)

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                PushNotificationSideEffect.NavigateBack -> onNavigateBack()
                PushNotificationSideEffect.MarketingPushEnabled -> {
                    onShowSnackbar(marketingEnabledMessage, null)
                }

                PushNotificationSideEffect.MarketingPushDisabled -> {
                    onShowSnackbar(marketingDisabledMessage, null)
                }

                PushNotificationSideEffect.NotificationEnabled -> {
                    onShowSnackbar(notificationEnabledMessage, null)
                }

                PushNotificationSideEffect.NotificationDisabled -> {
                    onShowSnackbar(notificationDisabledMessage, null)
                }
            }
        }
    }

    PushNotificationScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
