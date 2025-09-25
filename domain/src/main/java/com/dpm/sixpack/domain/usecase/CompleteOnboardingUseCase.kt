package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(): DoRunResult<Unit> {
        userPreferenceRepository.updateOnboardingComplete(isComplete = true)

        return DoRunResult.Success(Unit)
    }
}
