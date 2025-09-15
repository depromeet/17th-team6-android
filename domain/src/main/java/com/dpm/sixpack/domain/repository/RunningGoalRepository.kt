package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.RunningGoal
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningGoalRepository {
    suspend fun getTodayRunningGoal(): DoRunResult<RunningGoal>
}
