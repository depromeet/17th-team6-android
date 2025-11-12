package com.dpm.sixpack.data.source.remote.datasource

import com.dpm.sixpack.data.source.remote.dto.request.MyProfileUpdateRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.NewFcmTokenRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.MyProfileUpdateResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.UserProfileResponseDto
import com.dpm.sixpack.data.source.remote.service.UserService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

/**
 * 사용자 관련 Remote DataSource
 */
class UserDataSource @Inject constructor(
    private val userService: UserService,
    private val json: Json,
) {
    /**
     * 내 프로필 상세 조회
     *
     * @return 사용자 프로필 정보
     */
    suspend fun getMyProfile(): BaseResponse<UserProfileResponseDto> = userService.getMyProfile()

    /**
     * 내 프로필 수정
     *
     * @param nickname 닉네임 (2~8자)
     * @param imageOption 프로필 이미지 처리 옵션 (SET, REMOVE, KEEP)
     * @param profileImage 프로필 이미지 파일 (imageOption=SET인 경우 필수)
     * @return 수정된 프로필 정보
     */
    suspend fun updateMyProfile(
        nickname: String,
        imageOption: String,
        profileImage: File?,
    ): BaseResponse<MyProfileUpdateResponseDto> {
        // Request DTO 생성
        val requestDto =
            MyProfileUpdateRequestDto(
                nickname = nickname,
                imageOption = imageOption,
            )

        // DTO를 JSON 문자열로 변환하고 RequestBody로 변환
        val requestJson = json.encodeToString(requestDto)
        val requestBody = requestJson.toRequestBody("application/json".toMediaType())

        // 프로필 이미지가 있으면 MultipartBody.Part로 변환
        val imagePart =
            profileImage?.let { file ->
                val imageRequestBody = file.asRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData(
                    "profileImage",
                    file.name,
                    imageRequestBody,
                )
            }

        return userService.updateMyProfile(
            data = requestBody,
            profileImage = imagePart,
        )
    }

    suspend fun postNewFcmToken(token: NewFcmTokenRequestDto): BaseResponse<Unit> = userService.postNewFcmToken(token)
}
