package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.RunningSessionGoal
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningGoalRepository {
    suspend fun getTodayRunningGoal(): DoRunResult<RunningSessionGoal>
}
