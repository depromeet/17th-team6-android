package com.dpm.sixpack.domain.model.session

data class RunningSessionGoal(
    val id: Long,
    val createdAt: String,
    val updatedAt: String,
    val clearedAt: String?,
    val goalId: Long,
    val pace: Int,
    val distance: Int,
    val duration: Int,
    val roundCount: Int,
    val previousSessionId: Long,
)
