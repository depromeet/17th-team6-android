package com.dpm.sixpack.data.source.remote.datasoruce

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dpm.sixpack.domain.model.Friend
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 0
private const val NETWORK_PAGE_SIZE = 10

class FriendStatusPagingSource(
    private val friendDataSource: FriendDataSource,
    private val pageSize: Int = NETWORK_PAGE_SIZE,
) : PagingSource<Int, Friend>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Friend> {
        // 로드할 페이지 번호를 정합니다.
        // params.key가 null이면 첫 페이지(0)를 의미합니다.
        val pageNumber = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response =
                friendDataSource.getFriendsRunningStatus(
                    page = pageNumber,
                    size = pageSize,
                )

            val dataDto =
                response.data
                    ?: throw NullPointerException("Paging: 응답 데이터가 null입니다.")

            val contentList = dataDto.contents.map { it.toFriend() }
            val paginationInfo = dataDto.meta

            // 다음/이전 페이지 키를 계산합니다.
            val prevKey =
                if (paginationInfo.first) {
                    null // 첫 페이지면 이전 키는 null
                } else {
                    if (paginationInfo.hasPrevious) {
                        paginationInfo.page - 1 // 현재 페이지 - 1
                    } else {
                        null
                    }
                }

            val nextKey =
                if (paginationInfo.last) {
                    null // 마지막 페이지면 다음 키는 null
                } else {
                    if (paginationInfo.hasNext) {
                        paginationInfo.page + 1 // 현재 페이지 + 1
                    } else {
                        null
                    }
                }

            // 5. 성공 결과를 LoadResult.Page로 반환합니다.
            LoadResult.Page(
                data = contentList, // 실제 데이터 리스트
                prevKey = prevKey, // 이전 페이지 번호
                nextKey = nextKey, // 다음 페이지 번호
            )
        } catch (e: IOException) {
            // 네트워크 에러
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // HTTP 에러
            LoadResult.Error(e)
        } catch (e: Exception) {
            // 기타 모든 에러
            LoadResult.Error(e)
        }
    }

    /**
     * 목록이 새로고침(refresh)될 때 시작할 키(페이지)를 반환합니다.
     * (예: 스와이프 새로고침)
     */
    override fun getRefreshKey(state: PagingState<Int, Friend>): Int? =
        // 사용자가 보던 위치에서 가장 가까운 페이지를 반환할 수도 있습니다.
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}
