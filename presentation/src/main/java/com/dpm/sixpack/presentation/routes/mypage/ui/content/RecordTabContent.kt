package com.dpm.sixpack.presentation.routes.mypage.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.EmptyState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.ErrorState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.MonthNavigation
import com.dpm.sixpack.presentation.routes.mypage.ui.component.RecordCard
import com.dpm.sixpack.presentation.routes.mypage.ui.component.RecordTabLoadingState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecordTabContent(
    state: MyPageRecordTabState,
    onIntent: (MyPageRecordTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullToRefreshState()

    // 첫 로딩인지 판단 (데이터가 없으면서 로딩 중)
    val isInitialLoading = state.isLoading && state.records.isEmpty()

    // Pull-to-Refresh 로딩 중 (데이터가 있으면서 로딩 중)
    val isRefreshing = state.isLoading && state.records.isNotEmpty()

    when {
        isInitialLoading -> {
            RecordTabLoadingState(modifier = modifier)
        }

        state.error != null -> {
            Column(modifier = modifier) {
                // Month Navigation
                MonthNavigation(
                    yearMonth = state.currentYearMonth,
                    onPreviousClick = { onIntent(MyPageRecordTabIntent.OnPreviousMonthClick) },
                    onNextClick = { onIntent(MyPageRecordTabIntent.OnNextMonthClick) },
                    canGoPrevious = state.canGoPreviousMonth,
                    canGoNext = state.canGoNextMonth,
                )

                ErrorState(
                    message = state.error,
                    onRetry = { onIntent(MyPageRecordTabIntent.OnRetryClick) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        else -> {
            Column(modifier = modifier) {
                // Month Navigation
                MonthNavigation(
                    yearMonth = state.currentYearMonth,
                    onPreviousClick = { onIntent(MyPageRecordTabIntent.OnPreviousMonthClick) },
                    onNextClick = { onIntent(MyPageRecordTabIntent.OnNextMonthClick) },
                    canGoPrevious = state.canGoPreviousMonth,
                    canGoNext = state.canGoNextMonth,
                )

                // PullToRefreshBox로 콘텐츠 감싸기
                PullToRefreshBox(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { onIntent(MyPageRecordTabIntent.OnRefresh) },
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        Indicator(
                            state = pullRefreshState,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                            color = SixpackTheme.colors.blue600,
                            containerColor = Color.White,
                        )
                    },
                ) {
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
        }
    }
}
