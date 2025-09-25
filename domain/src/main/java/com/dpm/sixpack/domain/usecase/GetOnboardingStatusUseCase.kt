package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(): Boolean = userPreferenceRepository.getIsOnboardingComplete()
}
