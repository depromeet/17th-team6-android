package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class FinishRunningSessionUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(): DoRunResult<RunningSessionResult> {
        val sessionId =
            userPreferenceRepository.getSessionId()
                ?: return DoRunResult.Failure(DoRunException.DataError("저장된 세션 ID가 없어 종료할 수 없습니다."))

        return runningSessionRepository.finish(sessionId).onSuccess {
            userPreferenceRepository.clearSessionId()
        }
    }
}
