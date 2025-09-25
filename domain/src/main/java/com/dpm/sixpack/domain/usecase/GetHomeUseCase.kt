package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.home.Home
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetHomeUseCase @Inject constructor(
    private val runningGoalRepository: RunningGoalRepository,
) {
    suspend operator fun invoke(): DoRunResult<Home> {
        return withContext(Dispatchers.IO) {
            val total = async { runningGoalRepository.getRunningTotalGoal() }
            val todaySession = async { runningGoalRepository.getTodayRunningSessionGoal() }

            val totalResult = total.await()
            val todaySessionResult = todaySession.await()

            when {
                totalResult is DoRunResult.Success && todaySessionResult is DoRunResult.Success -> {
                    DoRunResult.Success(
                        Home(
                            runningTotalGoal = totalResult.data,
                            sessionGoal = todaySessionResult.data
                        )
                    )
                }

                else -> {
                    DoRunResult.Failure(DoRunException.UnknownError("Failed to get home data"))
                }
            }
        }
    }
}
