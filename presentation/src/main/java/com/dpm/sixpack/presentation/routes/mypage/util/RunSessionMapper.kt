package com.dpm.sixpack.presentation.routes.mypage.util

import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.presentation.routes.mypage.contract.CertificationStatus
import com.dpm.sixpack.presentation.routes.mypage.contract.RecordItem
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object RunSessionMapper {
    private val dayOfWeekKorean =
        mapOf(
            1 to "월",
            2 to "화",
            3 to "수",
            4 to "목",
            5 to "금",
            6 to "토",
            7 to "일",
        )

    /**
     * 날짜 문자열을 ZonedDateTime으로 파싱
     * ISO_DATE_TIME 형식(타임존 포함) 또는 ISO_LOCAL_DATE_TIME 형식(타임존 없음) 모두 지원
     */
    private fun parseDateTime(dateTimeString: String): ZonedDateTime =
        try {
            // 타임존 정보가 있는 경우 (예: 2025-11-07T15:39:31+09:00)
            ZonedDateTime.parse(dateTimeString)
        } catch (e: Exception) {
            // 타임존 정보가 없는 경우 (예: 2025-11-07T15:39:31.764631)
            // 시스템 기본 타임존으로 변환
            LocalDateTime.parse(dateTimeString).atZone(ZoneId.systemDefault())
        }

    fun RunSession.toRecordItem(): RecordItem {
        val finishedDateTime = parseDateTime(finishedAt)

        // Format: "2025.09.30 (화)"
        val dateFormatted =
            String.format(
                Locale.getDefault(),
                "%04d.%02d.%02d (%s)",
                finishedDateTime.year,
                finishedDateTime.monthValue,
                finishedDateTime.dayOfMonth,
                dayOfWeekKorean[finishedDateTime.dayOfWeek.value] ?: "",
            )

        // Format: "오전 10:11" or "오후 11:12"
        val timeFormatted =
            DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN).format(finishedDateTime)

        // Convert meters to km
        val distanceKm = distanceTotal / 1000.0

        // Convert seconds to "HH:MM:SS"
        val hours = durationTotal / 3600
        val minutes = (durationTotal % 3600) / 60
        val seconds = durationTotal % 60
        val durationFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

        // Convert seconds per km to "M'SS''"
        val paceMinutes = paceAvg / 60
        val paceSeconds = paceAvg % 60
        val paceFormatted = String.format(Locale.getDefault(), "%d'%02d''", paceMinutes, paceSeconds)

        // Determine certification status
        val certificationStatus =
            when {
                isSelfied -> CertificationStatus.COMPLETED
                distanceTotal >= 5000 -> CertificationStatus.AVAILABLE // Example: 5km or more can be certified
                else -> null
            }

        return RecordItem(
            id = runSessionId,
            date = dateFormatted,
            time = timeFormatted,
            distanceKm = distanceKm,
            durationFormatted = durationFormatted,
            paceFormatted = paceFormatted,
            cadence = cadenceAvg,
            certificationStatus = certificationStatus,
        )
    }

    fun List<RunSession>.toRecordItems(): List<RecordItem> = map { it.toRecordItem() }
}
