package com.dpm.sixpack.domain.usecase.user

import com.dpm.sixpack.domain.model.ProfileImageOption
import com.dpm.sixpack.domain.model.ProfileUpdateResponse
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import java.io.File
import javax.inject.Inject

/**
 * 프로필 수정 UseCase
 */
class UpdateProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        /**
         * 프로필 수정 실행
         *
         * @param nickname 닉네임 (2~8자)
         * @param imageOption 프로필 이미지 처리 옵션
         * @param profileImage 프로필 이미지 파일 (imageOption=SET인 경우 필수)
         * @return 수정된 프로필 정보
         */
        suspend operator fun invoke(
            nickname: String,
            imageOption: ProfileImageOption,
            profileImage: File?,
        ): DoRunResult<ProfileUpdateResponse> =
            userRepository.updateMyProfile(
                nickname = nickname,
                imageOption = imageOption,
                profileImage = profileImage,
            )
    }
