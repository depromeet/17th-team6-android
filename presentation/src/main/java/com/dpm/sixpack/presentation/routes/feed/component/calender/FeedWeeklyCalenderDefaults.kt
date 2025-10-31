package com.dpm.sixpack.presentation.routes.feed.component.calender

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 주간 달력 컴포넌트([FeedWeeklyCalendar])의 색상을 정의하는 데이터 클래스
 *
 * 이 클래스의 인스턴스는 [FeedWeeklyCalendarDefaults.colors] 함수를 통해 생성할 수 있습니다.
 *
 * @param calendarBackgroundColor 달력 컴포넌트 전체의 배경색.
 * @param weeklyDateTextColor '월', '화' 등 요일 텍스트의 색상.
 * @param headerNavigationIconColor 헤더의 '이전'/'다음' 탐색 아이콘(<, >) 색상.
 * @param headerDateColor 헤더의 '연/월' 텍스트 색상.
 *
 * [날짜 배경색]
 * @param selectedDateBackgroundColor 선택된 날짜 셀의 배경색.
 * @param defaultDateBackgroundColor 기본 상태(선택되지 않은) 날짜 셀의 배경색.
 * @param disabledDateBackgroundColor 비활성화된(선택 불가능한) 날짜 셀의 배경색.
 *
 * [날짜 텍스트색]
 * @param selectedDateTextColor 선택된 날짜 셀 내부 텍스트의 색상.
 * @param defaultDateTextColor 기본 상태 날짜 셀 내부 텍스트의 색상.
 * @param disabledDateTextColor 비활성화된 날짜 셀 내부 텍스트의 색상.
 *
 * [운동 횟수 텍스트색]
 * @param defaultFeedCountTextColor 기본 상태 날짜의 '일'(숫자) 텍스트 색상.
 * @param disabledFeedCountTextColor 비활성화된 날짜의 '일'(숫자) 텍스트 색상.
 */
@Immutable
data class WeeklyCalendarColors(
    // Common colors
    val calendarBackgroundColor: Color,
    val weeklyDateTextColor: Color,
    val headerNavigationIconColor: Color,
    val headerDateColor: Color,
    // Date Background Colors
    val selectedDateBackgroundColor: Color,
    val defaultDateBackgroundColor: Color,
    // Date Text Colors
    val selectedDateTextColor: Color,
    val defaultDateTextColor: Color,
    val disabledDateTextColor: Color,
    // Feed Count Text Colors
    val defaultFeedCountTextColor: Color,
    val disabledFeedCountTextColor: Color,
) {
    /**
     * 날짜 셀의 선택 여부에 따라 적절한 배경색을 반환하는 함수.
     *
     * @param isSelected 해당 날짜 셀이 선택되었는지 여부.
     * @return 계산된 [Color] 값.
     */
    fun dateBackgroundColor(isSelected: Boolean): Color =
        when {
            isSelected -> selectedDateBackgroundColor
            else -> defaultDateBackgroundColor
        }

    /**
     * 날짜 셀의 선택 여부와 활성화 여부에 따라 적절한 텍스트 색상을 반환하는 함수.
     *
     * @param isSelected 해당 날짜 셀이 선택되었는지 여부.
     * @param isDisabled 해당 날짜가 활성화되었는지 여부.
     * @return 계산된 [Color] 값.
     */
    fun dateTextColor(
        isSelected: Boolean,
        isDisabled: Boolean,
    ): Color =
        when {
            isDisabled -> disabledDateTextColor
            isSelected -> selectedDateTextColor
            else -> defaultDateTextColor
        }

    /**
     * 날짜 셀의 선택 여부에 따라 적절한 날짜 텍스트 색상을 반환하는 함수
     *
     * @param isDisabled 해당 날짜 셀이 비활성화되었는지 여부.
     * @return 계산된 [Color] 값.
     */
    fun feedCountTextColor(isDisabled: Boolean): Color =
        when {
            isDisabled -> disabledFeedCountTextColor
            else -> defaultFeedCountTextColor
        }
}

/**
 * 주간 달력 컴포넌트([FeedWeeklyCalendar])의 텍스트 스타일을 정의하는 데이터 클래스
 *
 * 이 클래스의 인스턴스는 [FeedWeeklyCalendarDefaults.typography] 함수를 통해 생성할 수 있습니다..
 * @property headerDateTextStyle 달력 헤더의 년/월 텍스트 스타일입니다.
 */
@Immutable
data class WeeklyCalendarTypography(
    val weeklyDateTextStyle: TextStyle,
    val dayCellDateTextStyle: TextStyle,
    val dayCountTextStyle: TextStyle,
    val headerDateTextStyle: TextStyle,
)

/**
 * [FeedWeeklyCalendar] 컴포저블의 디자인 기본값들을 제공하는 `object`
 */
object FeedWeeklyCalendarDefaults {
    /**
     * [FeedWeeklyCalendar]의 기본 색상 구성을 생성하는 함수
     *
     * 각 색상 파라미터는 [SixpackTheme]에서 기본값을 가져오며, 필요에 따라 재정의할 수 있습니다([copy]).
     */
    @Composable
    fun colors(
        // Common colors
        calendarBackgroundColor: Color = SixpackTheme.colors.gray0,
        weeklyDateTextColor: Color = SixpackTheme.colors.gray500,
        headerNavigationIconColor: Color = SixpackTheme.colors.gray800,
        headerDateColor: Color = SixpackTheme.colors.gray900,
        // Date Background Colors
        selectedDateBackgroundColor: Color = SixpackTheme.colors.blue600,
        defaultDateBackgroundColor: Color = SixpackTheme.colors.gray0,
        // Date Text Colors
        selectedDateTextColor: Color = SixpackTheme.colors.gray0,
        defaultDateTextColor: Color = SixpackTheme.colors.gray900,
        disabledDateTextColor: Color = SixpackTheme.colors.gray400,
        // Feed Count Text Colors
        defaultFeedCountTextColor: Color = SixpackTheme.colors.blue600,
        disabledFeedCountTextColor: Color = SixpackTheme.colors.gray400,
    ): WeeklyCalendarColors =
        remember(
            calendarBackgroundColor,
            weeklyDateTextColor,
            headerNavigationIconColor,
            headerDateColor,
            selectedDateBackgroundColor,
            defaultDateBackgroundColor,
            selectedDateTextColor,
            defaultDateTextColor,
            disabledDateTextColor,
            defaultFeedCountTextColor,
            disabledFeedCountTextColor,
        ) {
            WeeklyCalendarColors(
                calendarBackgroundColor = calendarBackgroundColor,
                weeklyDateTextColor = weeklyDateTextColor,
                headerNavigationIconColor = headerNavigationIconColor,
                headerDateColor = headerDateColor,
                selectedDateBackgroundColor = selectedDateBackgroundColor,
                defaultDateBackgroundColor = defaultDateBackgroundColor,
                selectedDateTextColor = selectedDateTextColor,
                defaultDateTextColor = defaultDateTextColor,
                disabledDateTextColor = disabledDateTextColor,
                defaultFeedCountTextColor = defaultFeedCountTextColor,
                disabledFeedCountTextColor = disabledFeedCountTextColor,
            )
        }

    /**
     * [FeedWeeklyCalendar]의 기본 텍스트 스타일 구성을 생성하는 함수.
     *
     * 각 텍스트 스타일 파라미터는 [SixpackTheme]에서 기본값을 가져오며, 필요에 따라 재정의할 수 있습니다([copy]).
     *
     * @param weeklyDateTextStyle 요일 텍스트 스타일입니다.
     * @param dayCellDateTextStyle 날짜 텍스트 스타일입니다.
     * @param dayCountTextStyle 운동 횟수 텍스트 스타일입니다.
     * @param headerDateTextStyle 달력 헤더의 년/월 텍스트 스타일입니다.
     * @return [WeeklyCalendarTypography] 인스턴스를 반환합니다.
     */
    @Composable
    fun typography(
        weeklyDateTextStyle: TextStyle = SixpackTheme.typography.b2Medium,
        dayCellDateTextStyle: TextStyle = SixpackTheme.typography.c1Medium,
        dayCountTextStyle: TextStyle = SixpackTheme.typography.c1Medium,
        headerDateTextStyle: TextStyle = SixpackTheme.typography.t2Bold,
    ): WeeklyCalendarTypography =
        remember(
            weeklyDateTextStyle,
            dayCellDateTextStyle,
            dayCountTextStyle,
            headerDateTextStyle,
        ) {
            WeeklyCalendarTypography(
                weeklyDateTextStyle = weeklyDateTextStyle,
                dayCellDateTextStyle = dayCellDateTextStyle,
                dayCountTextStyle = dayCountTextStyle,
                headerDateTextStyle = headerDateTextStyle,
            )
        }
}
