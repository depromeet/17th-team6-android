package com.dpm.sixpack.presentation.routes.report.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.model.SessionDetailFeed
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.FullScreenLoadingIndicator
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.report.component.ReportBottomBar
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun SessionReportScreen(
    sessionId: Long,
    state: ReportState,
    onIntent: (ReportIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = {
                    onIntent(ReportIntent.NavigateBack)
                },
                titleContent = {
                },
            )
        },
        bottomBar = {
            if (state is ReportState.Success) {
                if (state.sessionDetail.feed != null) {
                    ReportBottomBar(
                        modifier =
                            Modifier
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 24.dp),
                        onClick = {
                            onIntent(ReportIntent.NavigateToPostEdit(sessionId))
                        },
                    )
                }
            }
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        when (state) {
            ReportState.Loading -> {
                FullScreenLoadingIndicator(
                    alpha = 0.0f,
                )
            }

            is ReportState.Success -> {
                val sessionDetail = state.sessionDetail
                SessionReportScreenSuccessContent(
                    sessionDetail = sessionDetail,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(all = 24.dp),
                )
            }

            ReportState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.data_load_failed),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray700,
                    )

                    TextButton(
                        onClick = {
                            onIntent(ReportIntent.LoadSessionDetail(sessionId))
                        },
                        colors =
                            ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = SixpackTheme.colors.gray700,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(all = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.request_retry),
                                style = SixpackTheme.typography.b1Regular,
                                color = SixpackTheme.colors.gray700,
                            )
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = SixpackTheme.colors.gray700,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RunningRecordDetailScreenPreview() {
    DoRunPreviewWrapper {
        val sampleSessionDetail =
            SessionDetail(
                id = 1,
                createdAt = "2025-10-09T10:11:00Z",
                updatedAt = "2025-10-09T10:11:00Z",
                finishedAt = "2025-10-09T10:11:00Z",
                distanceTotal = 8020, // 8.02km
                durationTotal = 6726, // 1시간 52분 6초
                paceAvg = 450, // 7분 30초/km
                paceMax = 300,
                paceMaxLatitude = 0.0,
                paceMaxLongitude = 0.0,
                cadenceAvg = 144,
                cadenceMax = 180,
                mapImage = "https://i.ibb.co/L5k6h6f/map-example.png", // 실제 이미지 URL
                feed = null, // 인증하지 않은 상태
                segments = emptyList(),
            )

        // 인증한 상태 프리뷰 (SessionDetailFeed가 null이 아님)
        val sampleSessionDetailWithFeed =
            sampleSessionDetail.copy(
                feed =
                    SessionDetailFeed(
                        id = 1,
                        mapImage = "...",
                        selfieImage = "...",
                        content = "오늘 러닝 최고!",
                        createdAt = "...",
                    ),
            )

        Column {
//            SessionReportScreen(
//                sessionId = 123,
//                state =
//                    ReportState.Success(
//                        sessionDetail = sampleSessionDetail,
//                    ),
//                onIntent = { },
//            )
//            Spacer(modifier = Modifier.height(20.dp))
            SessionReportScreen(
                sessionId = 123,
                state =
                    ReportState.Success(
                        sessionDetail = sampleSessionDetailWithFeed,
                    ),
                onIntent = { /* Handle intents */ },
            )
        }
    }
}
