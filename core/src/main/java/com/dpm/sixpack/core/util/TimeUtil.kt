package com.dpm.sixpack.core.util

import timber.log.Timber
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

object TimeUtil {
    /**
     * Convert ms to "HH:mm:ss" or "mm:ss"
     */
    fun formatMillisWithDuration(millis: Long): String {
        val safeMillis = if (millis < 0) 0 else millis

        val duration = Duration.ofMillis(safeMillis)

        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        return if (hours > 0) {
            val paddedMinutes = minutes.toString().padStart(2, '0')
            val paddedSeconds = seconds.toString().padStart(2, '0')
            "$hours:$paddedMinutes:$paddedSeconds"
        } else {
            val totalMinutes = duration.toMinutes()
            val paddedSeconds = seconds.toString().padStart(2, '0')
            "$totalMinutes:$paddedSeconds"
        }
    }

    fun isoStringToEpochSeconds(timestamp: String): Long? =
        try {
            val instant = Instant.parse(timestamp)
            instant.epochSecond
        } catch (e: DateTimeParseException) {
            Timber.e("Error parsing timestamp: $timestamp")
            e.printStackTrace()
            null
        }
}
