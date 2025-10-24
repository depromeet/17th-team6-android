package com.dpm.sixpack.presentation.routes.feed.component.calender

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalendarUiState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle.*
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.text.get

private const val PAGER_PAGE_COUNT = Int.MAX_VALUE

private const val INITIAL_PAGE_INDEX = Int.MAX_VALUE / 2

private const val DAYS_IN_WEEK = 7

private const val HEADER_DATE_PATTERN = "M'월' W'주차'"


@Immutable
private data class WeeklyCalendarDay(
    val date: LocalDate,
    val isToday: Boolean,
    val isDisabled: Boolean,
    val postCount: Int
)


@Composable
fun FeedWeeklyCalendar(
    modifier: Modifier = Modifier,
    feedCalenderUiState: FeedCalendarUiState = FeedCalendarUiState(),
    startDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    colors: WeeklyCalendarColors = FeedWeeklyCalendarDefaults.colors(),
    typography: WeeklyCalendarTypography = FeedWeeklyCalendarDefaults.typography(),
    onDateSelected: (LocalDate) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val selectedDate = feedCalenderUiState.selectedDate
    val today = feedCalenderUiState.today

    val pagerState =
        rememberPagerState(
            initialPage = INITIAL_PAGE_INDEX,
            pageCount = { PAGER_PAGE_COUNT },
        )

    val firstDayOfInitialPagerWeek =
        remember(key1 = today, key2 = startDayOfWeek) {
            today.startOfWeek(startDayOfWeek)
        }

    val currentDisplayWeekStartDate by remember {
        derivedStateOf {
            val weeksOffset = pagerState.currentPage - INITIAL_PAGE_INDEX
            firstDayOfInitialPagerWeek.plusWeeks(weeksOffset.toLong())
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.calendarBackgroundColor)
    ) {
        WeeklyCalendarHeader(
            currentDisplayWeekViewStartDate = currentDisplayWeekStartDate,
            selectedDate = selectedDate,
            onPreviousWeek = {
                if (pagerState.currentPage > 0) {
                    scope.launch {
                        pagerState.animateScrollToPage(page = pagerState.currentPage - 1)
                    }
                }
            },
            onNextWeek = {
                if (pagerState.currentPage < PAGER_PAGE_COUNT - 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                    }
                }
            },
            colors = colors,
            typography = typography,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { pageIndex ->
            val weeksOffset = pageIndex - INITIAL_PAGE_INDEX
            val firstDayForThisPage =
                remember(key1 = firstDayOfInitialPagerWeek, key2 = weeksOffset) {
                    firstDayOfInitialPagerWeek.plusWeeks(weeksOffset.toLong())
                }
            val currentPageWeekDays by remember(
                key1 = firstDayForThisPage,
                key2 = today,
                key3 = feedCalenderUiState.postCounts
            ) {
                derivedStateOf {
                    generateWeekDaysList(
                        startDateOfWeek = firstDayForThisPage,
                        today = today,
                        postCounts = feedCalenderUiState.postCounts
                    )
                }
            }

            WeekRow(
                days = currentPageWeekDays,
                selectedDate = selectedDate,
                onDateClick = onDateSelected,
                colors = colors,
                typography = typography,
            )
        }
    }
}

/**
 * 주간 캘린더의 헤더를 표시하는 컴포저블
 *
 * 현재 표시된 주의 월 정보, 이전/다음 주 이동 아이콘을 포함합니다.
 */
@Composable
private fun WeeklyCalendarHeader(
    currentDisplayWeekViewStartDate: LocalDate,
    selectedDate: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    colors: WeeklyCalendarColors,
    typography: WeeklyCalendarTypography,
) {
    val displayDateForMonth = currentDisplayWeekViewStartDate.getDisplayMonth(selectedDate)

    val displayFormatter = remember { DateTimeFormatter.ofPattern(HEADER_DATE_PATTERN, Locale.getDefault()) }
    val displayText =
        remember(displayDateForMonth) {
            displayDateForMonth.format(displayFormatter)
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = displayText,
            style = typography.headerDateTextStyle,
            color = colors.headerDateColor,
        )

        Spacer(Modifier.weight(1f))


        Box(
            modifier = Modifier
                .noRippleClickable(onClick = onPreviousWeek)
                .padding(3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_left),
                contentDescription = "",
                tint = colors.headerNavigationIconColor,
            )
        }

        Box(
            modifier = Modifier
                .noRippleClickable(onClick = onNextWeek)
                .padding(3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                contentDescription = "",
                tint = colors.headerNavigationIconColor,
            )
        }
    }
}

/**
 * 한 주의 날짜들을 가로로 나열하여 표시하는 컴포저블
 */
@Composable
private fun WeekRow(
    days: List<WeeklyCalendarDay>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    colors: WeeklyCalendarColors,
    typography: WeeklyCalendarTypography,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        days.forEach { day ->
            val isSelected = day.date == selectedDate

            key(day.date) {
                DayCell(
                    dayData = day,
                    isSelected = isSelected,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDateClick(day.date)
                    },
                    colors = colors,
                    typography = typography,
                )
            }
        }
    }
}

/**
 * 주간 캘린더 내의 개별 날짜 셀(요일과 일자 포함)을 표시하는 컴포저블입니다.
 */
@Composable
private fun DayCell(
    dayData: WeeklyCalendarDay,
    isSelected: Boolean,
    colors: WeeklyCalendarColors,
    typography: WeeklyCalendarTypography,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val countBackgroundColor =
        colors.dayCountBackgroundColor(isSelected = isSelected, isDisabled = dayData.isDisabled)
    val countTextColor = colors.dayCountTextColor(isSelected = isSelected, isDisabled = dayData.isDisabled)
    val dateTextColor = colors.dayCellDateTextColor(isSelected = isSelected, isDisabled = dayData.isDisabled)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .noRippleClickable(enabled = !dayData.isDisabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = dayData.date.dayOfWeek.getDisplayName(
                SHORT,
                Locale.getDefault()
            ),
            color = dateTextColor,
            style = typography.weeklyDateTextStyle,
        )

        Spacer(modifier = Modifier.height(8.dp))

        DayFeedCount(
            count = dayData.postCount,
            backgroundColor = countBackgroundColor,
            textColor = countTextColor,
            typography = typography.dayCountTextStyle,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = dayData.date.dayOfMonth.toString(),
            color = dateTextColor,
            style = typography.dayCellDateTextStyle,
        )
    }
}

@Composable
fun DayFeedCount(
    count: Int,
    backgroundColor: Color,
    textColor: Color,
    typography: TextStyle,
    modifier: Modifier = Modifier,
) {
    val text = if (count > 0) stringResource(R.string.feed_calender_post_count_label, count) else count.toString()
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
                .clip(shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 5.dp),
            color = textColor,
            style = typography,
        )
    }
}

/**
 * 주간 캘린더 헤더에 표시할 월(Month)을 결정하는 확장 함수
 *
 * 주가 두 달에 걸쳐 있을 경우, 선택된 날짜([selectedDate])가 포함된 월을 우선적으로 표시합니다.
 */
private fun LocalDate.getDisplayMonth(selectedDate: LocalDate): LocalDate {
    val lastDayOfWeek = this.plusDays(DAYS_IN_WEEK - 1L)
    return if (this.month != lastDayOfWeek.month) {
        if (selectedDate.isAfter(this) && selectedDate.isBefore(lastDayOfWeek) && selectedDate.month == lastDayOfWeek.month) {
            selectedDate
        } else {
            this
        }
    } else {
        this
    }
}

/**
 * 특정 주의 시작 날짜([startDateOfWeek])와 "오늘"([today])을 기준으로
 * 해당 주의 [WeeklyCalendarDay] 목록을 생성하는 함수
 */

private fun generateWeekDaysList(
    startDateOfWeek: LocalDate,
    today: LocalDate,
    postCounts: Map<LocalDate, Int>,
): List<WeeklyCalendarDay> {
    // TODO SB 디자이너 요구에 맞춰서 isDisabled 수정
    return List(DAYS_IN_WEEK) { i ->
        val date = startDateOfWeek.plusDays(i.toLong())
        val postCount = postCounts[date] ?: 0

        WeeklyCalendarDay(
            date = date,
            isToday = date.isEqual(today),
            postCount = postCount,
            isDisabled = date.isAfter(today) || postCount == 0,
        )
    }
}

private fun LocalDate.startOfWeek(startDayOfWeek: DayOfWeek): LocalDate {
    val weekFields = WeekFields.of(startDayOfWeek, 1)

    return this.with(weekFields.dayOfWeek(), 1L)
}

@Preview(showBackground = true, name = "Feed Weekly Calendar")
@Composable
fun WeeklyCalendarPreview() {
    DoRunPreviewWrapper {
        Column(
            modifier =
                Modifier
                    .padding(20.dp),
        ) {
            val today = LocalDate.now()
            val selectedDate = today.minusDays(1)
            val postCounts = mapOf(
                today.minusDays(2) to 3,
                selectedDate to 1,
                today to 5
            )

            FeedWeeklyCalendar(
                feedCalenderUiState = FeedCalendarUiState(
                    today = today,
                    selectedDate = selectedDate,
                    postCounts = postCounts
                ),
                onDateSelected = {
                },
            )
        }
    }

}

