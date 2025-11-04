package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import javax.inject.Inject

/**
 * 사용자 로그인 상태를 확인하는 UseCase
 *
 * @return 로그인 상태 (true: 로그인됨, false: 미로그인)
 */
class CheckUserLoggedInUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(): Boolean {
        val userId = userPreferenceRepository.getUserId()
        return userId > 0
    }
}
