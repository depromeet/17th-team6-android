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
