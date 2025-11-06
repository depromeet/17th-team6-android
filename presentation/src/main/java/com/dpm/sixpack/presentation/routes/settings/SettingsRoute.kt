package com.dpm.sixpack.presentation.routes.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsSideEffect
import com.dpm.sixpack.presentation.routes.settings.ui.screen.SettingsScreen
import androidx.core.net.toUri

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToAccountInfo: () -> Unit,
    onNavigateToPushNotification: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

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
                SettingsSideEffect.ShowLogoutDialog -> showLogoutDialog = true
                SettingsSideEffect.ShowWithdrawDialog -> showWithdrawDialog = true
                SettingsSideEffect.LogoutSuccess -> {
                    showLogoutDialog = false
                    onShowSnackbar(logoutSuccessMessage, null)
                }
                SettingsSideEffect.LogoutFailed -> {
                    showLogoutDialog = false
                    onShowSnackbar(logoutErrorMessage, null)
                }
                SettingsSideEffect.WithdrawSuccess -> {
                    showWithdrawDialog = false
                    onShowSnackbar(withdrawSuccessMessage, null)
                }
                SettingsSideEffect.WithdrawFailed -> {
                    showWithdrawDialog = false
                    onShowSnackbar(withdrawErrorMessage, null)
                }
            }
        }
    }

    SettingsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        showLogoutDialog = showLogoutDialog,
        showWithdrawDialog = showWithdrawDialog,
        onDismissLogoutDialog = { showLogoutDialog = false },
        onDismissWithdrawDialog = { showWithdrawDialog = false },
        modifier = modifier,
    )
}
