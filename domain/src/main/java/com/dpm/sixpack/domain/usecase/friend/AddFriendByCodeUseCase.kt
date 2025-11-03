package com.dpm.sixpack.domain.usecase.friend

import com.dpm.sixpack.domain.repository.FriendRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class AddFriendByCodeUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(code: String): DoRunResult<String> = friendRepository.addFriendByCode(code)
}
