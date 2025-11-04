package com.dpm.sixpack.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dpm.sixpack.data.paging.FeedPagingSource
import com.dpm.sixpack.data.source.remote.datasoruce.FeedDataSource
import com.dpm.sixpack.data.source.remote.dto.request.UpdateSelfieRequestDto
import com.dpm.sixpack.data.source.remote.util.ContentUriRequestBody
import com.dpm.sixpack.domain.event.FeedUpdateEvent
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.CertifiedUser
import com.dpm.sixpack.domain.model.ReactionResult
import com.dpm.sixpack.domain.model.SelfieCounts
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedDataSource: FeedDataSource,
    private val contentResolver: ContentResolver,
) : FeedRepository {
    private val _feedUpdateEvents =
        MutableSharedFlow<FeedUpdateEvent>(
            replay = 1,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    override val feedUpdateEvents: SharedFlow<FeedUpdateEvent> = _feedUpdateEvents.asSharedFlow()

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
                    pageSize = 10,
                    initialLoadSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                FeedPagingSource(feedDataSource, feedType, currentDate, userId)
            },
        ).flow

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
                // Deleted 이벤트는 발생시키지 않음 (Optimistic UI로 처리)
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

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun updateSelfie(
        feedId: Long,
        content: String,
        imageUri: Uri?,
        deleteSelfieImage: Boolean?,
    ): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // UpdateSelfieRequestDto 생성
                val requestDto =
                    UpdateSelfieRequestDto(
                        content = content,
                        deleteSelfieImage = deleteSelfieImage,
                    )

                val json = Json.encodeToString(requestDto)
                val dataRequestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                // 이미지 URI를 MultipartBody.Part로 변환 (자동 압축)
                val imagePart =
                    imageUri?.let { uri ->
                        ContentUriRequestBody(contentResolver, uri).toFormData("selfieImage")
                    }

                // API 호출
                feedDataSource.updateSelfie(feedId, dataRequestBody, imagePart)

                // 수정 성공 이벤트 발생
                _feedUpdateEvents.emit(FeedUpdateEvent.Updated(feedId))

                DoRunResult.Success(Unit)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("게시물 수정에 실패했습니다: ${e.message}"))
            }
        }
}
