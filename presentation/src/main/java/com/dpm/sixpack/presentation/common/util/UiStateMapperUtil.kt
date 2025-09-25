package com.dpm.sixpack.presentation.common.util

import kotlin.math.abs

fun formatSecondsToTime(totalSeconds: Int): String {
    val seconds = abs(totalSeconds)

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

fun formatSecondsToPace(totalSeconds: Int): String {
    val seconds = abs(totalSeconds)

    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d'%02d", minutes, remainingSeconds)
}

fun calculatePace(
    totalTimeInSeconds: Int,
    distanceInKm: Double,
): String {
    if (distanceInKm <= 0) return "00:00"

    val paceInSeconds = (totalTimeInSeconds / distanceInKm).toInt()
    val minutes = paceInSeconds / 60
    val seconds = paceInSeconds % 60

    return String.format("%02d'%02d", minutes, seconds)
}

fun formatDistanceToKm(distanceInMeters: Int): String {
    if (distanceInMeters < 1000) return "${distanceInMeters}m"
    val km = distanceInMeters / 1000.0
    val formattedString = String.format("%.2f", km)

    return if (formattedString.endsWith("0")) {
        "${formattedString.dropLast(1)}km"
    } else {
        "${formattedString}km"
    }
}
