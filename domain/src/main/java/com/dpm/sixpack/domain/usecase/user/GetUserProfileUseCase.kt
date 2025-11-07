package com.dpm.sixpack.domain.usecase.user

import com.dpm.sixpack.domain.model.UserProfile
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

/**
 * 사용자 프로필 조회 UseCase
 */
class GetUserProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        /**
         * 사용자 프로필 조회 실행
         *
         * @return 사용자 프로필 정보
         */
        suspend operator fun invoke(): DoRunResult<UserProfile> = userRepository.getMyProfile()
    }
