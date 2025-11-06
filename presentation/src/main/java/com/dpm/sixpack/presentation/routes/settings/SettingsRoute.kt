package com.dpm.sixpack.presentation.routes.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsSideEffect
import com.dpm.sixpack.presentation.routes.settings.ui.screen.SettingsScreen

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToAccountInfo: () -> Unit,
    onNavigateToPushNotification: () -> Unit,
    onShowLogoutDialog: () -> Unit,
    onShowWithdrawDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                SettingsSideEffect.NavigateBack -> onNavigateBack()
                SettingsSideEffect.NavigateToProfileEdit -> onNavigateToProfileEdit()
                SettingsSideEffect.NavigateToAccountInfo -> onNavigateToAccountInfo()
                SettingsSideEffect.NavigateToPushNotification -> onNavigateToPushNotification()
                is SettingsSideEffect.NavigateToExternalUrl -> {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(sideEffect.url)).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    context.startActivity(intent)
                }
                SettingsSideEffect.ShowLogoutDialog -> onShowLogoutDialog()
                SettingsSideEffect.ShowWithdrawDialog -> onShowWithdrawDialog()
            }
        }
    }

    SettingsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
