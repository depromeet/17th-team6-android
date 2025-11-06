package com.dpm.sixpack.presentation.routes.settings.pushnotification.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.settings.NotificationSettingItem
import com.dpm.sixpack.presentation.common.components.settings.SettingsDivider
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTitleTopBar
import com.dpm.sixpack.presentation.routes.settings.pushnotification.contract.PushNotificationIntent
import com.dpm.sixpack.presentation.routes.settings.pushnotification.contract.PushNotificationState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun PushNotificationScreen(
    state: PushNotificationState,
    onIntent: (PushNotificationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunTitleTopBar(
                title = stringResource(R.string.settings_push_notification),
                onBackClick = { onIntent(PushNotificationIntent.OnBackButtonClick) },
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
            Spacer(modifier = Modifier.height(8.dp))

            // 마케팅 푸시
            NotificationSettingItem(
                title = stringResource(R.string.settings_push_marketing),
                description =
                    if (state.marketingPushConsentDate != null) {
                        stringResource(R.string.settings_push_marketing_consent_date, state.marketingPushConsentDate)
                    } else {
                        stringResource(R.string.settings_push_marketing_description)
                    },
                checked = state.isMarketingPushEnabled,
                onCheckedChange = { onIntent(PushNotificationIntent.OnMarketingPushToggle(it)) },
            )

            Spacer(modifier = Modifier.height(8.dp))
            SettingsDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // 알림 받기
            NotificationSettingItem(
                title = stringResource(R.string.settings_push_notification_enable),
                description = stringResource(R.string.settings_push_notification_enable_description),
                checked = state.isNotificationEnabled,
                onCheckedChange = { onIntent(PushNotificationIntent.OnNotificationToggle(it)) },
            )
        }
    }
}

@Preview
@Composable
private fun PushNotificationScreenPreview() {
    DoRunPreviewWrapper {
        PushNotificationScreen(
            state =
                PushNotificationState(
                    isMarketingPushEnabled = true,
                    isNotificationEnabled = true,
                    marketingPushConsentDate = "2025.10.27",
                ),
            onIntent = {},
        )
    }
}
