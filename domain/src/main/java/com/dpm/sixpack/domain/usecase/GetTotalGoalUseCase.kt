package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningGoalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTotalGoalUseCase @Inject constructor(
    private val runningGoalRepository: RunningGoalRepository,
) {
    suspend operator fun invoke() = runningGoalRepository.getRunningTotalGoal()
}
