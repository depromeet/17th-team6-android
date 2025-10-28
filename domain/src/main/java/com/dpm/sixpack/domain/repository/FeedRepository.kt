package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.FeedPage
import com.dpm.sixpack.domain.util.DoRunResult

interface FeedRepository {
    suspend fun getFeeds(
        currentDate: String?,
        userId: Int?,
        pageNum: Int,
        pageSize: Int,
    ): DoRunResult<FeedPage>
}
