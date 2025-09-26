package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.params.SaveTotalGoalParams
import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import com.dpm.sixpack.domain.model.total.RunningTotalGoal
import com.dpm.sixpack.domain.usecase.SaveTotalGoalUseCase
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningGoalRepository {
    suspend fun getRunningTotalGoal(): DoRunResult<RunningTotalGoal>

    suspend fun getTodayRunningSessionGoal(): DoRunResult<RunningSessionGoal>

    // TODO 프리런칭 이후 pagination 적용
    suspend fun getRunningSessions(goalId: Long): DoRunResult<List<RunningSessionGoal>>

    suspend fun saveRunningTotalGoal(goal: SaveTotalGoalParams)
}
