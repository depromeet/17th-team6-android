package com.dpm.sixpack.domain.usecase.user

import com.dpm.sixpack.domain.repository.UserRepository
import javax.inject.Inject

class SaveFcmTokenUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(token: String) = userRepository.updateFcmDeviceToken(token)
}
