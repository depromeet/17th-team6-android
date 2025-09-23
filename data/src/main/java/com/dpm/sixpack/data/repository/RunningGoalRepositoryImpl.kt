package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.remote.datasoruce.RunningGoalDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RunningSessionGoal
import com.dpm.sixpack.domain.repository.RunningGoalRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RunningGoalRepositoryImpl @Inject constructor(
    private val runningGoalDataSource: RunningGoalDataSource,
) : RunningGoalRepository {
    override suspend fun getTodayRunningGoal(): DoRunResult<RunningSessionGoal> =
        withContext(Dispatchers.IO) {
            try {
                val response = runningGoalDataSource.getTodayRunningGoal()

                val runningGoal =
                    response.data?.toRunningGoal()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                DoRunResult.Success(runningGoal)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }
}
