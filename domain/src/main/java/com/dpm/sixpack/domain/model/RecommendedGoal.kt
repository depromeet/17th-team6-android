package com.dpm.sixpack.domain.model

data class RecommendedGoal(
    val title: String,
    val subTitle: String,
    val type: String,
    val goal: Goal,
)
