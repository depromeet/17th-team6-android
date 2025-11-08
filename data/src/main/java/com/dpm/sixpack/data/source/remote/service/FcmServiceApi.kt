package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.AUTH
import retrofit2.http.POST

interface FcmServiceApi {
    // TODO SK: FCM 토큰 갱신 API 명세 아직 안나옴
    @POST("$API/$AUTH/refresh/")
    suspend fun postNewFcmToken(): BaseResponse<Nothing>
}
