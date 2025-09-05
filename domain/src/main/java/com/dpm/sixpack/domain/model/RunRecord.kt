package com.dpm.sixpack.domain.model

data class RunRecord(
    val sessionId: Long,
    val startTime: Long,
    val endTime: Long,
    val durationMs: String,
    val course: Course?
) {
    val distanceMeter: Double = course?.distanceMeter ?: 0.0
}
