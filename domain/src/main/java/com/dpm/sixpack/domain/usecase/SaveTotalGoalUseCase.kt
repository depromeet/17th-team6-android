package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.params.SaveTotalGoalParams
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveTotalGoalUseCase @Inject constructor(
    private val runningGoalRepository: RunningGoalRepository,
) {
    suspend operator fun invoke(saveTotalGoalParams: SaveTotalGoalParams): DoRunResult<Unit> =
        try {
            runningGoalRepository.saveRunningTotalGoal(saveTotalGoalParams)
            DoRunResult.Success(Unit)
        } catch (e: Exception) {
            DoRunResult.Failure(DoRunException.UnknownError(e.message.toString(),e))
        }
}
