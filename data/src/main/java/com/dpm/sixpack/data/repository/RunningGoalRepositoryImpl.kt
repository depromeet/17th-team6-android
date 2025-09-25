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
    override suspend fun getTodayRunningSessionGoal(): DoRunResult<RunningSessionGoal> =
        DoRunResult.Failure(DoRunException.DataError("미구현 상태입니다."))

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

    override suspend fun getRunningSessions(goalId: Long): DoRunResult<List<RunningSessionGoal>> =
        DoRunResult.Failure(DoRunException.DataError("미구현 상태입니다."))
}

class MockRunningGoalRepositoryImpl @Inject constructor() : RunningGoalRepository {
    override suspend fun getRunningTotalGoal(): DoRunResult<RunningTotalGoal> =
        DoRunResult.Success(
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
                clearedRoundCount = 2,
            ),
        )

    override suspend fun getTodayRunningSessionGoal(): DoRunResult<RunningSessionGoal> =
        DoRunResult.Success(
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
                goalId = 1,
            ),
        )

    override suspend fun getRunningSessions(goalId: Long): DoRunResult<List<RunningSessionGoal>> =
        DoRunResult.Success(
            (1..30).map { index ->
                RunningSessionGoal(
                    id = index.toLong(),
                    createdAt = "2023-10-10T10:00:00Z",
                    updatedAt = "2023-10-10T10:00:00Z",
                    clearedAt = if (index <= 5) "2023-10-10T10:00:00Z" else null,
                    pace = 320 + index * 10,
                    distance = 3000 + index * 300,
                    duration = 1200 + index * 120,
                    roundCount = index,
                    previousSessionId = index.toLong() - 1,
                    goalId = goalId,
                )
            }
        )
}
