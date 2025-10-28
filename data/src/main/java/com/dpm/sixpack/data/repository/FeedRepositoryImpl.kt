package com.dpm.sixpack.data.repository

import androidx.room.util.query
import com.dpm.sixpack.data.source.remote.datasoruce.FeedDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.FeedPage
import com.dpm.sixpack.domain.model.ReactionResult
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedDataSource: FeedDataSource,
) : FeedRepository {
    override suspend fun getFeeds(
        currentDate: String?,
        userId: Int?,
        pageNum: Int,
        pageSize: Int,
    ): DoRunResult<FeedPage> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    feedDataSource.getFeeds(currentDate = currentDate, userId = userId, page = pageNum, size = pageSize)

                val feeds = response.data?.toDomain() ?: throw DoRunException.DataError("데이터 변환에 실패했습니다")
                DoRunResult.Success(feeds)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }


    override suspend fun postReaction(
        selfieId: Int,
        emojiType: String
    ): DoRunResult<ReactionResult> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    feedDataSource.postReaction( selfieId, emojiType )
                val reactionResult = response.data?.toDomain() ?: throw DoRunException.DataError("데이터 변환에 실패했습니다")
                DoRunResult.Success(reactionResult)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }

}
