package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.response.MyProfileUpdateResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.UserProfileResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

/**
 * 사용자 관련 API Service
 */
interface UserService {
    /**
     * 내 프로필 상세 조회
     *
     * @return 사용자 프로필 정보
     */
    @GET("/api/users/me/profile")
    suspend fun getMyProfile(): BaseResponse<UserProfileResponseDto>

    /**
     * 내 프로필 수정
     *
     * @param data 프로필 수정 데이터 (nickname, imageOption을 JSON으로 포함)
     * @param profileImage 프로필 이미지 파일 (imageOption=SET인 경우 필수)
     * @return 수정된 프로필 이미지 URL
     */
    @Multipart
    @PATCH("/api/users/me")
    suspend fun updateMyProfile(
        @Part("data") data: RequestBody,
        @Part profileImage: MultipartBody.Part?,
    ): BaseResponse<MyProfileUpdateResponseDto>
}
