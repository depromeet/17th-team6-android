package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
data class SessionReportRoute(
    val sessionId: Long,
) : Route
