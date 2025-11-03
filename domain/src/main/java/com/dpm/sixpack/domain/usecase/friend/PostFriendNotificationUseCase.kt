package com.dpm.sixpack.domain.usecase.friend

import com.dpm.sixpack.domain.repository.FriendRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class PostFriendNotificationUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(friendUserId: Long): DoRunResult<Unit> =
        friendRepository.postFriendNotification(friendUserId)
}
