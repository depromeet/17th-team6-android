package com.dpm.sixpack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dpm.sixpack.data.source.remote.datasoruce.FriendDataSource
import com.dpm.sixpack.data.source.remote.datasoruce.FriendStatusPagingSource
import com.dpm.sixpack.data.source.remote.dto.request.AddFriendRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.FriendDeleteRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.FriendNotificationRequestDto
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.Friend
import com.dpm.sixpack.domain.repository.FriendRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val friendDataSource: FriendDataSource,
) : FriendRepository {
    companion object {
        private const val FRIEND_PAGE_SIZE = 20
    }

    override fun getFriendsRunningStatusPager(): Flow<PagingData<Friend>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = FRIEND_PAGE_SIZE,
                    enablePlaceholders = false, // 로드되지 않은 항목을 null로 표시할지 여부
                ),
            pagingSourceFactory = {
                FriendStatusPagingSource(friendDataSource, FRIEND_PAGE_SIZE)
            },
        ).flow

    override suspend fun postFriendNotification(friendUserId: Long): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val requestDto = FriendNotificationRequestDto(userId = friendUserId)

                friendDataSource.postFriendNotification(requestDto)

                DoRunResult.Success(Unit)
            } catch (e: HttpException) {
                DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("친구 응원 실패: ${e.message}"))
            }
        }

    override suspend fun deleteFriend(friendUidList: List<Long>): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val requestDto = FriendDeleteRequestDto(friendIds = friendUidList)

                friendDataSource.deleteFriend(requestDto)

                DoRunResult.Success(Unit)
            } catch (e: HttpException) {
                DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("친구 삭제 실패: ${e.message}"))
            }
        }

    override suspend fun getMyFriendCode(): DoRunResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = friendDataSource.getMyFriendCode()

                val code =
                    response.data?.code
                        ?: throw DoRunException.DataError("서버로부터 친구 코드 데이터를 받지 못했습니다.")

                DoRunResult.Success(code)
            } catch (e: HttpException) {
                DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("친구 코드 조회 실패: ${e.message}"))
            }
        }

    override suspend fun addFriendByCode(code: String): DoRunResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val requestDto = AddFriendRequestDto(code = code)

                val response = friendDataSource.addFriendByCode(requestDto)

                val nickname =
                    response.data?.nickname
                        ?: throw DoRunException.DataError("서버로부터 친구 추가 응답을 받지 못했습니다.")

                DoRunResult.Success(nickname)
            } catch (e: HttpException) {
                DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("친구 추가 실패: ${e.message}"))
            }
        }
}
