package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningGoalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSessionListUseCase @Inject constructor(
    private val runningGoalRepository: RunningGoalRepository,
) {
    suspend operator fun invoke(goalId: Long) = runningGoalRepository.getRunningSessions(goalId)
}
