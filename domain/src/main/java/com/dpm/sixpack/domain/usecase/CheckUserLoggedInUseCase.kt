package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.UserRepository
import javax.inject.Inject

/**
 * 사용자 로그인 상태를 확인하는 UseCase
 *
 * userId와 accessToken을 모두 확인하여 로그인 상태를 판단합니다.
 * 둘 중 하나라도 없으면 로그인되지 않은 것으로 간주합니다.
 *
 * @return 로그인 상태 (true: 로그인됨, false: 미로그인)
 */
class CheckUserLoggedInUseCase @Inject constructor(
    private val userPreferenceRepository: UserRepository,
) {
    suspend operator fun invoke(): Boolean {
        val userId = userPreferenceRepository.getUserId()
        val accessToken = userPreferenceRepository.getAccessToken()

        // userId와 accessToken이 모두 있어야 로그인된 것으로 간주
        return userId > 0 && !accessToken.isNullOrBlank()
    }
}
