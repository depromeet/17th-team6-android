package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.FeedPage
import com.dpm.sixpack.domain.model.ReactionResult
import com.dpm.sixpack.domain.model.SelfieCount
import com.dpm.sixpack.domain.model.SelfieCounts
import com.dpm.sixpack.domain.util.DoRunResult

interface FeedRepository {
    suspend fun getFeeds(
        currentDate: String?,
        userId: Int?,
        pageNum: Int,
        pageSize: Int,
    ): DoRunResult<FeedPage>

    suspend fun postReaction(
        selfieId: Int,
        emojiType: String
    ): DoRunResult<ReactionResult>

    suspend fun getSelfieCalendar(
        startDate: String,
        endDate: String
    ): DoRunResult<SelfieCounts>
}
