package com.dpm.sixpack.domain.usecase.running

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRunningDataUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(isPaused: Boolean): DoRunResult<SaveRealtimeRunningDataResult.SyncResult> {
        val sessionId =
            userPreferenceRepository.getSessionId()
                ?: return DoRunResult.Failure(DoRunException.DataError("저장된 세션 ID가 없습니다."))

        return runningSessionRepository.syncSegmentData(sessionId, isPaused)
    }
}
