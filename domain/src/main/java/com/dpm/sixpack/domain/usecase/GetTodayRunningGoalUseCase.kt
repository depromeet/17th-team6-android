package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.RunningGoal
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class GetTodayRunningGoalUseCase
    @Inject
    constructor(
        private val runningGoalRepository: RunningGoalRepository,
    ) {
        suspend operator fun invoke(userId: Long): DoRunResult<RunningGoal> =
            runningGoalRepository.getTodayRunningGoal(userId = userId)
    }
