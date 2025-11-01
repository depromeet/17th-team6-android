package com.dpm.sixpack.presentation.common.util.format

import kotlin.math.abs

fun formatSecondsToTime(totalSeconds: Int): String {
    val seconds = abs(totalSeconds)
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

fun formatPace(paceInSeconds: Int): String {
    val minutes = paceInSeconds / 60
    val seconds = paceInSeconds % 60

    return String.format("%d'%02d\"", minutes, seconds)
}

fun formatPhoneNumber(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }

    return when {
        digitsOnly.length <= 3 -> digitsOnly
        digitsOnly.length <= 7 -> {
            // 010-1234 형식
            "${digitsOnly.substring(0, 3)}-${digitsOnly.substring(3)}"
        }
        digitsOnly.length <= 10 -> {
            // 010-123-4567 형식 (10자리)
            "${digitsOnly.substring(0, 3)}-${digitsOnly.substring(3, 6)}-${digitsOnly.substring(6)}"
        }
        else -> {
            // 010-1234-5678 형식 (11자리)
            val trimmed = digitsOnly.take(11)
            "${trimmed.substring(0, 3)}-${trimmed.substring(3, 7)}-${trimmed.substring(7)}"
        }
    }
}
