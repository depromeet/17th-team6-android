package com.dpm.sixpack.domain.repository

import androidx.paging.PagingData
import com.dpm.sixpack.domain.model.FeedContent
import com.dpm.sixpack.domain.model.ReactionResult
import com.dpm.sixpack.domain.model.SelfieCounts
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeedPagingStream(
        pageSize : Int,
        initialLoadSize : Int,
        currentDate: String?,
        userId: Long?
    ): Flow<PagingData<FeedContent>>

    suspend fun postReaction(
        selfieId: Int,
        emojiType: String
    ): DoRunResult<ReactionResult>

    suspend fun getSelfieCalendar(
        startDate: String,
        endDate: String
    ): DoRunResult<SelfieCounts>
}
