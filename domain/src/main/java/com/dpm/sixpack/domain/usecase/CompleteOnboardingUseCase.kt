package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.params.SaveTotalGoalParams
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val runningGoalRepository: RunningGoalRepository,
) {
    suspend operator fun invoke(params: SaveTotalGoalParams): DoRunResult<Unit> {
        return try {
            runningGoalRepository.saveRunningTotalGoal(params)
            userPreferenceRepository.updateOnboardingComplete(isComplete = true)
            DoRunResult.Success(Unit)
        } catch (e: Exception) {
            DoRunResult.Failure(DoRunException.UnknownError(e.message.toString(), e))
        }
    }
}
