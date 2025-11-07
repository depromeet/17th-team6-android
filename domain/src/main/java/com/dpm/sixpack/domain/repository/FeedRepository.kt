package com.dpm.sixpack.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.dpm.sixpack.domain.event.FeedUpdateEvent
import com.dpm.sixpack.domain.model.CertifiedUser
import com.dpm.sixpack.domain.model.Feed
import com.dpm.sixpack.domain.model.ReactionResult
import com.dpm.sixpack.domain.model.SelfieCounts
import com.dpm.sixpack.domain.model.UserSummary
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface FeedRepository {
    val feedUpdateEvents: SharedFlow<FeedUpdateEvent>

    fun getFeedPagingStream(
        pageSize: Int,
        initialLoadSize: Int,
        feedType: FeedType,
        currentDate: String?,
        userId: Long?,
    ): Flow<PagingData<FeedListItem>>

    suspend fun getFeedDetail(feedId: Long): DoRunResult<Feed>

    suspend fun getUserSummary(userId: Long?): DoRunResult<UserSummary>

    suspend fun postReaction(
        selfieId: Long,
        emojiType: String,
    ): DoRunResult<ReactionResult>

    suspend fun getCertifiedUsers(date: String): DoRunResult<List<CertifiedUser>>

    suspend fun getSelfieCalendar(
        startDate: String,
        endDate: String,
    ): DoRunResult<SelfieCounts>

    suspend fun uploadPost(
        sessionId: Long,
        content: String?,
        imageUri: Uri?,
    ): DoRunResult<Unit>

    suspend fun updatePost(
        feedId: Long,
        content: String,
        imageUri: Uri?,
        deleteSelfieImage: Boolean?,
    ): DoRunResult<Unit>

    suspend fun deletePost(feedId: Long): DoRunResult<Unit>
}

sealed interface FeedListItem {
    data class UserSummaryItem(
        val summary: UserSummary,
    ) : FeedListItem

    data class PostItem(
        val feed: Feed,
    ) : FeedListItem
}

enum class FeedType {
    MAIN_FEED,
    USER_PAGE_FEED,
}
