package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class SaveRealtimeRunningDataUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(param: SaveRealtimeRunningDataParam): DoRunResult<SaveRealtimeRunningDataResult> =
        when (param) {
            is SaveRealtimeRunningDataParam.LocalParam -> {
                runningSessionRepository.saveRealtimeDataOnLocal(
                    data = param.data,
                )
            }

            is SaveRealtimeRunningDataParam.SyncParam -> {
                val sessionId =
                    userPreferenceRepository.getSessionId()
                        ?: return DoRunResult.Failure(
                            DoRunException.DataError("저장된 세션 ID가 없어 동기화할 수 없습니다."),
                        )

                runningSessionRepository.saveSegmentData(sessionId)
            }
        }
}
