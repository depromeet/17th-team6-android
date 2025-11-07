package com.dpm.sixpack.presentation.routes.feed.certifiablerecord.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.record.DateGroupedRecords
import com.dpm.sixpack.presentation.common.components.record.dateGroupedRecordItems
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.model.RecordItem
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordIntent
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun CertifiableRecordScreen(
    state: CertifiableRecordUiState,
    onIntent: (CertifiableRecordIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmpty = state.records.isEmpty()
    val groupedRecords =
        remember(state.records) {
            DateGroupedRecords.fromRecords(state.records)
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(CertifiableRecordIntent.OnBackClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.post_upload_certifiable_record_title),
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900,
                    modifier = Modifier,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.post_upload_certifiable_record_subtitle),
                    style = SixpackTheme.typography.b2Regular,
                    color = SixpackTheme.colors.gray900,
                    modifier = Modifier,
                )

                if (!isEmpty) {
                    Spacer(Modifier.height(32.dp))

                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        dateGroupedRecordItems(
                            groupedRecords = groupedRecords,
                            selectedRecordId = state.selectedRecord?.sessionId,
                            onRecordClick = { record ->
                                onIntent(CertifiableRecordIntent.OnRecordClick(record))
                            },
                        )
                    }
                } else {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        EmptyRecord()
                    }
                }
            }

            DoRunDefaultButton(
                text = stringResource(id = R.string.post_upload_certifiable_record_submit_button),
                enabled = state.selectedRecord != null,
                onClick = { onIntent(CertifiableRecordIntent.OnUploadClick) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun EmptyRecord(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.img_feed_empty_expired),
            contentDescription = stringResource(id = R.string.post_upload_certifiable_record_empty_image_description),
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.post_upload_certifiable_record_empty_title),
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.post_upload_certifiable_record_empty_subtitle),
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CertifiableRecordScreenPreview() {
    DoRunPreviewWrapper {
        CertifiableRecordScreen(
            state =
                CertifiableRecordUiState(
                    records =
                        listOf(
                            RecordItem(
                                sessionId = 1,
                                runningSummary =
                                    RunningSummary(
                                        totalDistance = "8.02km",
                                        totalTime = "01:12:03",
                                        averagePace = "6'74\"",
                                        cadence = "128",
                                        recordDateTime = "2025.10.15 · 오전 10:11",
                                    ),
                                mapImageUrl = "",
                                postTime = "2025-10-15T10:11:00",
                            ),
                            RecordItem(
                                sessionId = 2,
                                runningSummary =
                                    RunningSummary(
                                        totalDistance = "5.50km",
                                        totalTime = "00:45:20",
                                        averagePace = "8'14\"",
                                        cadence = "150",
                                        recordDateTime = "2025.10.14 · 오후 11:12",
                                    ),
                                mapImageUrl = "",
                                postTime = "2025-10-14T23:12:00",
                            ),
                            RecordItem(
                                sessionId = 3,
                                runningSummary =
                                    RunningSummary(
                                        totalDistance = "10.02km",
                                        totalTime = "01:30:45",
                                        averagePace = "9'03\"",
                                        cadence = "142",
                                        recordDateTime = "2025.10.14 · 오전 07:30",
                                    ),
                                mapImageUrl = "",
                                postTime = "2025-10-14T07:30:00",
                            ),
                        ),
                ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CertifiableRecordScreenEmptyPreview() {
    DoRunPreviewWrapper {
        CertifiableRecordScreen(
            state = CertifiableRecordUiState(),
            onIntent = {},
        )
    }
}
