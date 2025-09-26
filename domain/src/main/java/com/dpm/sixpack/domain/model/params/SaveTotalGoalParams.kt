package com.dpm.sixpack.domain.model.params

data class SaveTotalGoalParams(
    val title: String,
    val subTitle: String,
    val type: String,
    val pace: Int,
    val distance: Int,
    val duration: Int,
    val totalRoundCount: Int,
)
