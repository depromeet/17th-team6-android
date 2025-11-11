package com.dpm.sixpack.presentation.common.components.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.RecordItem
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Immutable
data class DateGroupedRecords(
    val date: String,
    val records: List<RecordItem>,
) {
    companion object {
        fun fromRecords(records: List<RecordItem>): List<DateGroupedRecords> =
            records
                .groupBy { record ->
                    record.formattedDate
                }.map { (date, items) ->
                    DateGroupedRecords(
                        date = date,
                        records = items.sortedByDescending { it.postTime },
                    )
                }.sortedByDescending { it.date }
    }
}

/**
 * LazyListScope 확장 함수로 날짜별 그룹화된 러닝 기록을 추가합니다.
 * 각 날짜 헤더마다 해당 날짜의 RecordCard들이 표시됩니다.
 *
 * @param groupedRecords 날짜별로 그룹화된 러닝 기록 리스트
 * @param selectedRecordId 현재 선택된 레코드의 sessionId (null이면 선택 없음)
 * @param onRecordClick 기록 클릭 시 호출되는 콜백 (sessionId 전달)
 *
 */

fun LazyListScope.dateGroupedRecordItems(
    groupedRecords: List<DateGroupedRecords>,
    selectedRecordId: Long? = null,
    onRecordClick: (RecordItem) -> Unit,
) {
    groupedRecords.forEach { dateGroup ->
        item(key = "header_${dateGroup.date}") {
            DateHeaderItem(
                date = dateGroup.date,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        items(
            items = dateGroup.records,
            key = { record -> "record_${record.sessionId}" },
        ) { record ->
            RecordCard(
                record = record,
                modifier = Modifier.fillMaxWidth(),
                isSelected = record.sessionId == selectedRecordId,
                onClick = { onRecordClick(record) },
            )
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun DateHeaderItem(
    date: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = date,
        style = SixpackTheme.typography.b2Medium,
        color = SixpackTheme.colors.gray500,
        modifier = modifier,
    )
}

@Composable
private fun RecordCard(
    record: RecordItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val borderColor = if (isSelected) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray100
    Box(
        modifier =
            modifier
                .clip(SixpackTheme.shapes.round16)
                .background(SixpackTheme.colors.gray0)
                .border(width = 1.dp, color = borderColor, shape = SixpackTheme.shapes.round16)
                .noRippleClickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = record.formattedTime,
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray700,
            )

            Text(
                text = record.runningSummary.totalDistance,
                style = SixpackTheme.typography.h2Bold,
                color = SixpackTheme.colors.gray900,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RecordDetailItem(
                    value = record.runningSummary.totalTime,
                )

                VerticalDivider(
                    modifier =
                        Modifier
                            .height(14.dp),
                    thickness = 1.dp,
                    color = SixpackTheme.colors.gray100,
                )

                RecordDetailItem(
                    value = record.runningSummary.averagePace,
                )

                VerticalDivider(
                    modifier =
                        Modifier
                            .height(14.dp),
                    thickness = 1.dp,
                    color = SixpackTheme.colors.gray100,
                )

                RecordDetailItem(
                    value = record.runningSummary.cadence,
                )
            }
        }
    }
}

@Composable
private fun RecordDetailItem(
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = value,
        style = SixpackTheme.typography.b1Regular,
        color = SixpackTheme.colors.gray700,
        modifier = modifier,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun DateGroupedRecordListPreview() {
    DoRunPreviewWrapper {
        val mockRecords =
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
                            totalDistance = "8.02km",
                            totalTime = "01:12:03",
                            averagePace = "6'74\"",
                            cadence = "128",
                            recordDateTime = "2025.10.14 · 오후 11:12",
                        ),
                    mapImageUrl = "",
                    postTime = "2025-10-14T11:12:00",
                ),
                RecordItem(
                    sessionId = 3,
                    runningSummary =
                        RunningSummary(
                            totalDistance = "8.02km",
                            totalTime = "01:12:03",
                            averagePace = "6'74\"",
                            cadence = "128",
                            recordDateTime = "2025.10.14 · 오후 11:12",
                        ),
                    mapImageUrl = "",
                    postTime = "2025-10-14T11:12:00",
                ),
            )

        Column {
            DateHeaderItem(
                date = "2025.10.15",
            )

            Spacer(Modifier.height(12.dp))

            RecordCard(
                record =
                    RecordItem(
                        sessionId = 2,
                        runningSummary =
                            RunningSummary(
                                totalDistance = "8.02km",
                                totalTime = "01:12:03",
                                averagePace = "6'74\"",
                                cadence = "128",
                                recordDateTime = "2025.10.14 · 오후 11:12",
                            ),
                        mapImageUrl = "",
                        postTime = "2025-10-14T11:12:00",
                    ),
                isSelected = true,
                onClick = { /* Handle click event */ },
            )
        }
    }
}
