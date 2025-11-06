package com.dpm.sixpack.presentation.routes.settings.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.dialog.DialogButtonType
import com.dpm.sixpack.presentation.common.components.dialog.DoRunDefaultDialog
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.settings.SettingsDivider
import com.dpm.sixpack.presentation.common.components.settings.SettingsMenuItem
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTitleTopBar
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsIntent
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    showLogoutDialog: Boolean,
    showWithdrawDialog: Boolean,
    onDismissLogoutDialog: () -> Unit,
    onDismissWithdrawDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunTitleTopBar(
                title = stringResource(R.string.settings_title),
                onBackClick = { onIntent(SettingsIntent.OnBackButtonClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 계정 설정 섹션
            SettingsMenuItem(
                title = stringResource(R.string.settings_profile_edit),
                onClick = { onIntent(SettingsIntent.OnProfileEditClick) },
            )
            SettingsMenuItem(
                title = stringResource(R.string.settings_account_info),
                onClick = { onIntent(SettingsIntent.OnAccountInfoClick) },
            )
            SettingsMenuItem(
                title = stringResource(R.string.settings_push_notification),
                onClick = { onIntent(SettingsIntent.OnPushNotificationClick) },
            )

            Spacer(modifier = Modifier.height(8.dp))
            SettingsDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // 약관 섹션
            SettingsMenuItem(
                title = stringResource(R.string.settings_privacy_policy),
                onClick = { onIntent(SettingsIntent.OnPrivacyPolicyClick) },
            )
            SettingsMenuItem(
                title = stringResource(R.string.settings_terms),
                onClick = { onIntent(SettingsIntent.OnTermsClick) },
            )
            SettingsMenuItem(
                title = stringResource(R.string.settings_version),
                endContent = {
                    Text(
                        text = state.appVersion,
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.gray500,
                    )
                },
                showArrow = false,
                onClick = {},
            )

            Spacer(modifier = Modifier.height(8.dp))
            SettingsDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // 로그아웃 / 탈퇴 섹션
            SettingsMenuItem(
                title = stringResource(R.string.settings_logout),
                onClick = { onIntent(SettingsIntent.OnLogoutClick) },
                showArrow = false,
            )
            SettingsMenuItem(
                title = stringResource(R.string.settings_withdraw),
                onClick = { onIntent(SettingsIntent.OnWithdrawClick) },
                showArrow = false,
            )
        }
    }

    // 로그아웃 다이얼로그
    if (showLogoutDialog) {
        DoRunDefaultDialog(
            title = stringResource(R.string.settings_logout_dialog_title),
            subtitle = stringResource(R.string.settings_logout_dialog_subtitle),
            onDismissRequest = onDismissLogoutDialog,
            onCancelClick = onDismissLogoutDialog,
            cancelButtonText = stringResource(R.string.settings_logout_dialog_cancel),
            confirmButtonText = stringResource(R.string.settings_logout_dialog_confirm),
            onConfirmClick = { onIntent(SettingsIntent.OnLogoutConfirm) },
            confirmButtonType = DialogButtonType.Primary,
        )
    }

    // 회원 탈퇴 다이얼로그
    if (showWithdrawDialog) {
        DoRunDefaultDialog(
            title = stringResource(R.string.settings_withdraw_dialog_title),
            subtitle = stringResource(R.string.settings_withdraw_dialog_subtitle),
            onDismissRequest = onDismissWithdrawDialog,
            onCancelClick = onDismissWithdrawDialog,
            cancelButtonText = stringResource(R.string.settings_withdraw_dialog_cancel),
            confirmButtonText = stringResource(R.string.settings_withdraw_dialog_confirm),
            onConfirmClick = { onIntent(SettingsIntent.OnWithdrawConfirm) },
            confirmButtonType = DialogButtonType.Destructive,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    DoRunPreviewWrapper {
        SettingsScreen(
            state =
                SettingsState(
                    appVersion = "3.13.0",
                ),
            onIntent = {},
            showLogoutDialog = false,
            showWithdrawDialog = false,
            onDismissLogoutDialog = {},
            onDismissWithdrawDialog = {},
        )
    }
}
