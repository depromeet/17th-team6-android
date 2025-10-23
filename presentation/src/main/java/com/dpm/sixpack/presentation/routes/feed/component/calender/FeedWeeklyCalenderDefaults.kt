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
 * @param selectedCountCellBackgroundColor 선택된 날짜 셀의 배경색.
 * @param defaultCountCellBackgroundColor 기본 상태(선택되지 않은) 날짜 셀의 배경색.
 * @param disabledCountCellBackgroundColor 비활성화된(선택 불가능한) 날짜 셀의 배경색.
 * @param selectedCountCellTextColor 선택된 날짜 셀 내부 텍스트의 색상.
 * @param defaultCountCellTextColor 기본 상태 날짜 셀 내부 텍스트의 색상.
 * @param disabledCountCellTextColor 비활성화된 날짜 셀 내부 텍스트의 색상.
 * @param selectedDayCellDateTextColor 선택된 날짜의 '일'(숫자) 텍스트 색상.
 * @param defaultDayCellDateTextColor 기본 상태 날짜의 '일'(숫자) 텍스트 색상.
 * @param disabledDayCellDateTextColor 비활성화된 날짜의 '일'(숫자) 텍스트 색상.
 * @param headerNavigationIconColor 헤더의 '이전'/'다음' 탐색 아이콘(<, >) 색상.
 * @param headerDateColor 헤더의 '연/월' 텍스트 색상.
 */
@Immutable
data class WeeklyCalendarColors(
    val calendarBackgroundColor: Color,
    val weeklyDateTextColor: Color,
    val selectedCountCellBackgroundColor: Color,
    val defaultCountCellBackgroundColor: Color,
    val disabledCountCellBackgroundColor: Color,
    val selectedCountCellTextColor: Color,
    val defaultCountCellTextColor: Color,
    val disabledCountCellTextColor: Color,
    val selectedDayCellDateTextColor: Color,
    val defaultDayCellDateTextColor: Color,
    val disabledDayCellDateTextColor: Color,
    val headerNavigationIconColor: Color,
    val headerDateColor: Color,
) {
    /**
     * 날짜 셀의 선택 여부와 활성화 여부에 따라 적절한 배경색을 반환하는 함수.
     *
     * @param isSelected 해당 날짜 셀이 선택되었는지 여부.
     * @param isEnabled 해당 날짜가 활성화되었는지 여부.
     * @return 계산된 [Color] 값.
     */
    fun dayCountBackgroundColor(isSelected: Boolean, isEnabled: Boolean): Color {
        return when {
            !isEnabled -> disabledCountCellBackgroundColor
            isSelected -> selectedCountCellBackgroundColor
            else -> defaultCountCellBackgroundColor
        }
    }

    /**
     * 날짜 셀의 선택 여부와 활성화 여부에 따라 적절한 텍스트 색상을 반환하는 함수.
     *
     * @param isSelected 해당 날짜 셀이 선택되었는지 여부.
     * @param isEnabled 해당 날짜가 활성화되었는지 여부.
     * @return 계산된 [Color] 값.
     */
    fun dayCountTextColor(isSelected: Boolean, isEnabled: Boolean): Color {
        return when {
            !isEnabled -> disabledCountCellTextColor
            isSelected -> selectedCountCellTextColor
            else -> defaultCountCellTextColor
        }
    }

    /**
     * 날짜 셀의 선택 여부에 따라 적절한 날짜 텍스트 색상을 반환하는 함수
     *
     * @param isSelected 해당 날짜 셀이 선택되었는지 여부.
     * @return 계산된 [Color] 값.
     */
    fun dayCellDateTextColor(isSelected: Boolean, isEnabled: Boolean): Color {
        return when {
            !isEnabled -> disabledDayCellDateTextColor
            isSelected -> selectedDayCellDateTextColor
            else -> defaultDayCellDateTextColor
        }
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
     * 각 색상 파라미터는 [SixPackTheme]에서 기본값을 가져오며, 필요에 따라 재정의할 수 있습니다([copy]).
     */
    //TODO SB 색상 디자인에 맞춰 바꾸기
    @Composable
    fun colors(
        calendarBackgroundColor: Color = SixpackTheme.colors.gray0,
        weeklyDateTextColor: Color = SixpackTheme.colors.gray50,
        selectedCountCellBackgroundColor: Color = SixpackTheme.colors.blue900,
        defaultCountCellBackgroundColor: Color = SixpackTheme.colors.blue300,
        disabledCountCellBackgroundColor: Color = SixpackTheme.colors.gray100,
        selectedCountCellTextColor: Color = SixpackTheme.colors.gray0,
        defaultCountCellTextColor: Color = SixpackTheme.colors.blue900,
        disabledCountCellTextColor: Color = SixpackTheme.colors.gray300,
        selectedDayCellDateTextColor: Color = SixpackTheme.colors.blue600,
        defaultDayCellDateTextColor: Color = SixpackTheme.colors.gray900,
        disabledDayCellDateTextColor: Color = SixpackTheme.colors.gray100,
        headerNavigationIconColor: Color = SixpackTheme.colors.gray900,
        headerDateColor: Color = SixpackTheme.colors.gray900,
    ): WeeklyCalendarColors {
        return remember(
            calendarBackgroundColor,
            weeklyDateTextColor,
            selectedCountCellBackgroundColor,
            defaultCountCellBackgroundColor,
            disabledCountCellBackgroundColor,
            selectedCountCellTextColor,
            defaultCountCellTextColor,
            disabledCountCellTextColor,
            selectedDayCellDateTextColor,
            defaultDayCellDateTextColor,
            disabledDayCellDateTextColor,
            headerNavigationIconColor,
            headerDateColor,
        ) {
            WeeklyCalendarColors(
                calendarBackgroundColor = calendarBackgroundColor,
                weeklyDateTextColor = weeklyDateTextColor,
                selectedCountCellBackgroundColor = selectedCountCellBackgroundColor,
                defaultCountCellBackgroundColor = defaultCountCellBackgroundColor,
                disabledCountCellBackgroundColor = disabledCountCellBackgroundColor,
                selectedCountCellTextColor = selectedCountCellTextColor,
                defaultCountCellTextColor = defaultCountCellTextColor,
                disabledCountCellTextColor = disabledCountCellTextColor,
                selectedDayCellDateTextColor = selectedDayCellDateTextColor,
                defaultDayCellDateTextColor = defaultDayCellDateTextColor,
                disabledDayCellDateTextColor = disabledDayCellDateTextColor,
                headerNavigationIconColor = headerNavigationIconColor,
                headerDateColor = headerDateColor,
            )
        }
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
        dayCellDateTextStyle: TextStyle = SixpackTheme.typography.b2Medium,
        dayCountTextStyle: TextStyle = SixpackTheme.typography.c1Medium,
        headerDateTextStyle: TextStyle = SixpackTheme.typography.t2Bold,
    ): WeeklyCalendarTypography {
        return remember(
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
}
