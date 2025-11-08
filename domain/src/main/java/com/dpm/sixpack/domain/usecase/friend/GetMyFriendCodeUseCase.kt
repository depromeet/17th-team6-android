package com.dpm.sixpack.domain.usecase.friend

import com.dpm.sixpack.domain.repository.FriendRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class GetMyFriendCodeUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(): DoRunResult<String> = friendRepository.getMyFriendCode()
}
