package com.dpm.sixpack.presentation.routes.running.map

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb

object PaceColorCalculator {
    data class PaceColorPoint(
        val paceInSeconds: Int,
        val color: Color,
    )

    private val paceColorPoints =
        listOf(
            PaceColorPoint(300, Color(0xFF4751FF)), // 5:00
            PaceColorPoint(360, Color(0xFF26FF00)), // 6:00
            PaceColorPoint(420, Color(0xFFFFD700)), // 7:00
            PaceColorPoint(480, Color(0xFFFF7B00)), // 8:00
            PaceColorPoint(540, Color(0xFFFF0000)), // 9:00
        ).sortedBy { it.paceInSeconds }

    /**
     * 페이스(초/km) 값을 받아 그에 맞는 Color 객체를 반환
     */
    operator fun invoke(paceInSeconds: Int): Int {
        val calculatedColor: Color =
            when {
                // 1분 이하 -> 비정상인 값 투명처리
                paceInSeconds <= 60 -> {
                    Color.Transparent
                }
                // 가장 빠른 페이스보다 더 빠를 경우
                paceInSeconds <= paceColorPoints.first().paceInSeconds -> {
                    paceColorPoints.first().color
                }
                // 가장 느린 페이스보다 더 느릴 경우
                paceInSeconds >= paceColorPoints.last().paceInSeconds -> {
                    paceColorPoints.last().color
                }
                // 그 사이 구간일 경우
                else -> {
                    val (startPoint, endPoint) = findPaceSegment(paceInSeconds)
                    val segmentDuration = (endPoint.paceInSeconds - startPoint.paceInSeconds).toFloat()
                    val offsetFromStart = (paceInSeconds - startPoint.paceInSeconds).toFloat()
                    val fraction = offsetFromStart / segmentDuration
                    lerp(startPoint.color, endPoint.color, fraction)
                }
            }

        return calculatedColor.toArgb()
    }

    /**
     * 현재 페이스가 어떤 두 PaceColorPoint 사이에 위치하는지 찾아 반환
     */
    private fun findPaceSegment(paceInSeconds: Int): Pair<PaceColorPoint, PaceColorPoint> =
        paceColorPoints
            .windowed(2)
            .first { (start, end) ->
                paceInSeconds >= start.paceInSeconds && paceInSeconds < end.paceInSeconds
            }.let { (start, end) ->
                start to end
            }
}
