package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.remote.datasoruce.RunningSessionDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.repository.SessionDetailRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class SessionDetailRepositoryImpl @Inject constructor(
    private val sessionDataSource: RunningSessionDataSource,
) : SessionDetailRepository {
    override suspend fun getSessionDetail(sessionId: Long): DoRunResult<SessionDetail> =
        withContext(Dispatchers.IO) {
            try {
                val response = sessionDataSource.getSessionDetail(sessionId)
                val sessionDetail =
                    response.data?.toDomain() ?: throw DoRunException.DataError("서버로부터 세션 ID를 받지 못했습니다.")

                DoRunResult.Success(sessionDetail)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    DoRunResult.Failure(DoRunException.DataError("${e.code()} HTTP 에러: ${e.message}"))
                } else {
                    DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
                }
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.UnknownError(e.message.toString()))
            }
        }
}
