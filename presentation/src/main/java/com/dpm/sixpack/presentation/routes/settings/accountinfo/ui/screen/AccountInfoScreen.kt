package com.dpm.sixpack.presentation.routes.settings.accountinfo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTitleTopBar
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoIntent
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoState
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun AccountInfoScreen(
    state: AccountInfoState,
    onIntent: (AccountInfoIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunTitleTopBar(
                title = stringResource(R.string.settings_account_info),
                onBackClick = { onIntent(AccountInfoIntent.OnBackButtonClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when {
                state.isLoading -> {
                    // 로딩 중
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = SixpackTheme.colors.blue600,
                    )
                }

                state.errorMessage != null -> {
                    // 에러 발생
                    Text(
                        text = state.errorMessage,
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.red,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    // 정상 데이터 표시
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        // 가입 휴대폰 번호
                        AccountInfoItem(
                            label = stringResource(R.string.settings_account_phone_number),
                            value = state.phoneNumber,
                        )

                        // 가입일자
                        AccountInfoItem(
                            label = stringResource(R.string.settings_account_join_date),
                            value = state.joinDate,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = SixPackDimen.defaultSideMargin, vertical = 12.dp)
                .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray900,
        )

        Text(
            text = value,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray600,
        )
    }
}

@Preview
@Composable
private fun AccountInfoScreenPreview() {
    DoRunPreviewWrapper {
        AccountInfoScreen(
            state =
                AccountInfoState(
                    phoneNumber = "010-7724-8020",
                    joinDate = "2025.04.25",
                ),
            onIntent = {},
        )
    }
}
