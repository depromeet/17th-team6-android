package com.dpm.sixpack.domain.repository

import androidx.paging.PagingData
import com.dpm.sixpack.domain.model.Friend
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun postFriendNotification(friendUserId: Long): DoRunResult<String>

    suspend fun deleteFriend(friendUidList: List<Long>): DoRunResult<Unit>

    fun getFriendsRunningStatusPager(): Flow<PagingData<Friend>>

    suspend fun getMyFriendCode(): DoRunResult<String>

    suspend fun addFriendByCode(code: String): DoRunResult<String>
}
