package com.dpm.sixpack.domain.model.home

import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import com.dpm.sixpack.domain.model.total.RunningTotalGoal

data class Home(
    val runningTotalGoal: RunningTotalGoal,
    val sessionGoal: RunningSessionGoal
)
