package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import timber.log.Timber
import javax.inject.Inject

class StartRunningUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(): DoRunResult<Long> {
        val localSessionId = userPreferenceRepository.getSessionId()

        return if (localSessionId == null) {
            runningSessionRepository.startSession().onSuccess { newSessionId ->
                userPreferenceRepository.updateSessionId(newSessionId)
            }
        } else {
            Timber.d("기존 러닝을 다시 시작합니다.")
            DoRunResult.Success(localSessionId)
        }
    }
}
