package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.util.DoRunResult

interface SessionDetailRepository {
    suspend fun getSessionDetail(sessionId: Long): DoRunResult<SessionDetail>
}
