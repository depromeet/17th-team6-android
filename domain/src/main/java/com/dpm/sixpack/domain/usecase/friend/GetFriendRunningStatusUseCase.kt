package com.dpm.sixpack.domain.usecase.friend

import com.dpm.sixpack.domain.repository.FriendRepository
import javax.inject.Inject

class GetFriendRunningStatusUseCase @Inject constructor(
    private val repository: FriendRepository,
) {
    operator fun invoke() = repository.getFriendsRunningStatusPager()
}
