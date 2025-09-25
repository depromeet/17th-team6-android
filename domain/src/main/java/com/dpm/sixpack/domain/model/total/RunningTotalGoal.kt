package com.dpm.sixpack.domain.model.total

/**
 * 오늘의 러닝 목표 데이터 Entity
 */
data class RunningTotalGoal(
    val id: Long,
    val createdAt: String,
    val updatedAt: String,
    val pausedAt: String?,
    val clearedAt: String?,
    val title: String,
    val subTitle: String,
    val type: String,
    val pace: Int,
    val distance: Int,
    val duration: Int,
    val totalRoundCount: Int,
    val clearedRoundCount: Int
)
