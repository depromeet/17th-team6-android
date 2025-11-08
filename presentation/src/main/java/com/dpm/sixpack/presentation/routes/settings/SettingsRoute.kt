package com.dpm.sixpack.presentation.routes.settings

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsSideEffect
import com.dpm.sixpack.presentation.routes.settings.ui.screen.SettingsScreen

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToAccountInfo: () -> Unit,
    onNavigateToPushNotification: () -> Unit,
    onShowSnackbar: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    val logoutSuccessMessage = stringResource(R.string.settings_logout_success_snackbar)
    val logoutErrorMessage = stringResource(R.string.settings_logout_error_snackbar)
    val withdrawSuccessMessage = stringResource(R.string.settings_withdraw_success_snackbar)
    val withdrawErrorMessage = stringResource(R.string.settings_withdraw_error_snackbar)

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                SettingsSideEffect.NavigateBack -> onNavigateBack()
                SettingsSideEffect.NavigateToProfileEdit -> onNavigateToProfileEdit()
                SettingsSideEffect.NavigateToAccountInfo -> onNavigateToAccountInfo()
                SettingsSideEffect.NavigateToPushNotification -> onNavigateToPushNotification()
                is SettingsSideEffect.NavigateToExternalUrl -> {
                    val intent =
                        Intent(Intent.ACTION_VIEW, sideEffect.url.toUri()).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    context.startActivity(intent)
                }

                SettingsSideEffect.LogoutSuccess -> {
                    onShowSnackbar(logoutSuccessMessage, null)
                }

                SettingsSideEffect.LogoutFailed -> {
                    onShowSnackbar(logoutErrorMessage, null)
                }

                SettingsSideEffect.WithdrawSuccess -> {
                    onShowSnackbar(withdrawSuccessMessage, null)
                }

                SettingsSideEffect.WithdrawFailed -> {
                    onShowSnackbar(withdrawErrorMessage, null)
                }
            }
        }
    }

    SettingsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
