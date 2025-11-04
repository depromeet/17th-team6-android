package com.dpm.sixpack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dpm.sixpack.data.paging.FeedPagingSource
import com.dpm.sixpack.data.source.remote.datasoruce.FeedDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.CertifiedUser
import com.dpm.sixpack.domain.model.ReactionResult
import com.dpm.sixpack.domain.model.SelfieCounts
import com.dpm.sixpack.domain.model.UserSummary
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedDataSource: FeedDataSource,
) : FeedRepository {
    override fun getFeedPagingStream(
        pageSize: Int,
        initialLoadSize: Int,
        feedType: FeedType,
        currentDate: String?,
        userId: Long?,
    ): Flow<PagingData<FeedListItem>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10, // 2. 성능 튜닝 섹션에서 권장한 값
                    initialLoadSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                FeedPagingSource(feedDataSource, feedType, currentDate, userId)
            },
        ).flow

    override suspend fun getUserSummary(userId: Long?): DoRunResult<UserSummary> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    feedDataSource.getFeeds(
                        currentDate = null,
                        userId = userId,
                        page = 0,
                        size = 1,
                    )
                val feedPage = response.data?.toDomain() ?: throw DoRunException.DataError("데이터 변환에 실패했습니다")
                DoRunResult.Success(feedPage.contents.userSummary)
            } catch (e: DoRunException) {
                DoRunResult.Failure(e)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.NetworkError("유저 요약 정보 조회에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun postReaction(
        selfieId: Long,
        emojiType: String,
    ): DoRunResult<ReactionResult> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    feedDataSource.postReaction(selfieId, emojiType)
                val reactionResult = response.data?.toDomain() ?: throw DoRunException.DataError("데이터 변환에 실패했습니다")
                DoRunResult.Success(reactionResult)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun deleteFeed(feedId: Long): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                feedDataSource.deleteFeed(feedId)
                DoRunResult.Success(Unit)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("피드 삭제에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun getCertifiedUsers(date: String): DoRunResult<List<CertifiedUser>> =
        withContext(Dispatchers.IO) {
            try {
                val response = feedDataSource.getCertifiedUsers(date)
                val certifiedUsers = response.data?.toDomain() ?: throw DoRunException.DataError("데이터 변환에 실패했습니다")
                DoRunResult.Success(certifiedUsers)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("인증 유저 목록 조회에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun getSelfieCalendar(
        startDate: String,
        endDate: String,
    ): DoRunResult<SelfieCounts> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    feedDataSource.getSelfieWeek(startDate, endDate)

                val selfieCounts = response.data?.toDomain() ?: throw DoRunException.DataError("데이터 변환에 실패했습니다")
                DoRunResult.Success(selfieCounts)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }
}
