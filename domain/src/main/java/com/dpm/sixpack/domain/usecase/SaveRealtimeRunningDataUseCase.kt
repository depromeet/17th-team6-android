package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class SaveRealtimeRunningDataUseCase
@Inject
constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(param: SaveRealtimeRunningDataParam): DoRunResult<SaveRealtimeRunningDataResult> =
        when (param) {
            is SaveRealtimeRunningDataParam.LocalParam -> runningSessionRepository.saveRealtimeData(data = param.data)
            is SaveRealtimeRunningDataParam.SyncParam -> {
                val sessionId = userPreferenceRepository.getSessionId()
                    ?: return DoRunResult.Failure(DoRunException.DataError("저장된 세션 ID가 없어 동기화할 수 없습니다."))

                runningSessionRepository.saveSegmentData(sessionId)
            }
        }
}

sealed class SaveRealtimeRunningDataParam {
    data class LocalParam(
        val data: RealtimeRunningData,
    ) : SaveRealtimeRunningDataParam()

    data class SyncParam(
        val sessionId: Long,
    ) : SaveRealtimeRunningDataParam()
}

sealed class SaveRealtimeRunningDataResult {
    data object LocalResult : SaveRealtimeRunningDataResult()

    data class SyncResult(
        val segmentId: Long, // 구간 ID
        val savedCount: Int, // 저장된 데이터 개수
    ) : SaveRealtimeRunningDataResult()
}
