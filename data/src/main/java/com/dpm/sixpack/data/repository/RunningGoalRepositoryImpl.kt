package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.remote.datasoruce.RunningGoalDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import com.dpm.sixpack.domain.model.total.RunningTotalGoal
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RunningGoalRepositoryImpl @Inject constructor(
    private val runningGoalDataSource: RunningGoalDataSource,
) : RunningGoalRepository {
    override suspend fun getTodayRunningSessionGoal(): DoRunResult<RunningSessionGoal> {
        TODO("Not yet implemented")
    }

    override suspend fun getRunningTotalGoal(): DoRunResult<RunningTotalGoal> =
        withContext(Dispatchers.IO) {
            try {
                val response = runningGoalDataSource.getTodayRunningGoal()

                val runningGoal =
                    response.data?.toRunningTotalGoal()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                DoRunResult.Success(runningGoal)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }
}

class MockRunningGoalRepositoryImpl @Inject constructor() : RunningGoalRepository {
    override suspend fun getRunningTotalGoal(): DoRunResult<RunningTotalGoal> {
        return DoRunResult.Success(
            RunningTotalGoal(
                id = 1L,
                createdAt = "2023-10-10T10:00:00Z",
                updatedAt = "2023-10-10T10:00:00Z",
                pausedAt = null,
                clearedAt = null,
                title = "Mock Total Goal",
                subTitle = "This is a mock total goal",
                type = "distance",
                pace = 300,
                distance = 5000,
                duration = 1800,
                totalRoundCount = 5,
                clearedRoundCount = 2
            )
        )
    }

    override suspend fun getTodayRunningSessionGoal(): DoRunResult<RunningSessionGoal> {
        return DoRunResult.Success(
            RunningSessionGoal(
                id = 3L,
                createdAt = "2023-10-10T10:00:00Z",
                updatedAt = "2023-10-10T10:00:00Z",
                clearedAt = null,
                pace = 320,
                distance = 3000,
                duration = 1200,
                roundCount = 3,
                previousSessionId = 2L,
                goalId = 1
            )
        )
    }
}
