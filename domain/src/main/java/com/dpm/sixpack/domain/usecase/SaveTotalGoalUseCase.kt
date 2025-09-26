package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningGoalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveTotalGoalUseCase @Inject constructor(
    private val runningGoalRepository: RunningGoalRepository,
) {
    suspend operator fun invoke(params: Params): Result<Unit> =
        try {
            val currentGoal = runningGoalRepository.saveRunningTotalGoal(params)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    data class Params(
        val title: String,
        val subTitle: String,
        val type: String,
        val pace: Int,
        val distance: Int,
        val duration: Int,
        val totalRoundCount: Int,
    )
}
