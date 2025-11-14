package com.dpm.sixpack.presentation.routes.report.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.model.SessionDetailFeed
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.FullScreenLoadingIndicator
import com.dpm.sixpack.presentation.common.components.dialog.DoRunErrorScreen
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.report.component.ReportBottomBar
import com.dpm.sixpack.presentation.routes.report.component.ReportErrorScreen
import com.dpm.sixpack.presentation.routes.report.contract.ReportBottomBarType
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun SessionReportScreen(
    state: ReportState,
    onIntent: (ReportIntent) -> Unit,
    navigateToHome: () -> Unit = { },
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
                ReportBottomBar(
                    imageUrl = state.sessionDetail.feed?.selfieImage ?: state.sessionDetail.feed?.mapImage,
                    bottomBarType =
                        if (state.sessionDetail.feed != null) {
                            ReportBottomBarType.DETAIL
                        } else {
                            state.bottomBarType
                        },
                    modifier =
                        Modifier
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 12.dp),
                    onIntent = onIntent,
                )
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

            is ReportState.Error -> {
                if (state.code == 404) {
                    DoRunErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        title = stringResource(R.string.error_not_found),
                        description = stringResource(R.string.error_not_found_explanation),
                        confirmButtonText = stringResource(R.string.back_to_home),
                        onConfirmClick = navigateToHome,
                    )
                } else {
                    ReportErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        title = stringResource(R.string.error_network_connection),
                        description = stringResource(R.string.error_network_connection_explanation),
                        confirmButtonText = stringResource(R.string.retry),
                        onConfirmClick = {
                            onIntent(ReportIntent.LoadSessionDetail)
                        },
                        navigateToHome = navigateToHome,
                    )
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
                state =
                    ReportState.Success(
                        sessionDetail = sampleSessionDetailWithFeed,
                    ),
                onIntent = { /* Handle intents */ },
            )
        }
    }
}
