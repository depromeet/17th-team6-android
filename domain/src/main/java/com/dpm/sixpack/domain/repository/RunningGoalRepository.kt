package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import com.dpm.sixpack.domain.model.total.RunningTotalGoal
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningGoalRepository {
    suspend fun getRunningTotalGoal(): DoRunResult<RunningTotalGoal>

    suspend fun getTodayRunningSessionGoal(): DoRunResult<RunningSessionGoal>
}
