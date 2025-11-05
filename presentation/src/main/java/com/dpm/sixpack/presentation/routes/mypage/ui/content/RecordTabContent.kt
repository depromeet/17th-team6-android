package com.dpm.sixpack.presentation.routes.mypage.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.EmptyState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.MonthNavigation
import com.dpm.sixpack.presentation.routes.mypage.ui.component.RecordCard
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordTabContent(
    state: MyPageRecordTabState,
    onIntent: (MyPageRecordTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Month Navigation
        MonthNavigation(
            yearMonth = state.currentYearMonth,
            onPreviousClick = { onIntent(MyPageRecordTabIntent.OnPreviousMonthClick) },
            onNextClick = { onIntent(MyPageRecordTabIntent.OnNextMonthClick) },
            canGoPrevious = state.canGoPreviousMonth,
            canGoNext = state.canGoNextMonth,
        )

        if (state.records.isEmpty()) {
            EmptyState(
                title = "아직 러닝 기록이 없어요...",
                description = "지금 바로 러닝을 시작해봐요!",
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Group records by date
            val recordsByDate = state.records.groupBy { it.date }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(36.dp),
            ) {
                recordsByDate.forEach { (date, recordsForDate) ->
                    // Date group
                    item(key = "date_$date") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Date header
                            Text(
                                text = date,
                                style = SixpackTheme.typography.b2Medium,
                                color = SixpackTheme.colors.gray700,
                            )

                            // Records for this date
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                recordsForDate.forEach { record ->
                                    RecordCard(
                                        record = record,
                                        onClick = { onIntent(MyPageRecordTabIntent.OnRecordClick(record.id)) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
