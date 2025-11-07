package com.dpm.sixpack.domain.usecase.friend

import com.dpm.sixpack.domain.repository.FriendRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class DeleteFriendUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(friendUidList: List<Long>): DoRunResult<Unit> =
        friendRepository.deleteFriend(friendUidList)
}
