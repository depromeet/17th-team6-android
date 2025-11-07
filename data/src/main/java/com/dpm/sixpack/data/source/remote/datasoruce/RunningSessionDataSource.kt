package com.dpm.sixpack.data.source.remote.datasoruce

import android.content.Context
import android.graphics.Bitmap
import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.RunSessionListResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.service.RunningSessionServiceApi
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.domain.exception.DoRunException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RunningSessionDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val runningSessionServiceApi: RunningSessionServiceApi,
) {
    suspend fun postFinishRunning(
        sessionId: Long,
        finishRequestDto: FinishRunningRequestDto,
        mapImage: Bitmap,
    ): BaseResponse<FinishRunningResponseDto> {
        val dataRequestBody =
            Json
                .encodeToString(finishRequestDto.data)
                .toRequestBody("application/json".toMediaType())

        // Bitmap에서 임시 파일을 생성하고, MultipartBody.Part로 변환
        val (tempFile, mapImagePart) = createMultipartBodyPart(mapImage, "mapImage")

        try {
            return runningSessionServiceApi.postFinishRunning(
                sessionId = sessionId,
                data = dataRequestBody,
                mapImage = mapImagePart,
            )
        } finally {
            // API 호출이 끝나면 임시 파일 삭제
            tempFile.delete()
        }
    }

    suspend fun postSegmentData(
        sessionId: Long,
        saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto,
    ): BaseResponse<SaveSegmentResponseDto> =
        runningSessionServiceApi.postSegmentData(sessionId, saveSegmentDataRequestsDto)

    suspend fun postStartSession(): BaseResponse<StartRunningResponseDto> = runningSessionServiceApi.postStartSession()

    /**
     * Bitmap을 임시 파일로 변환하고 MultipartBody.Part를 생성합니다.
     * @param image API로 전송할 Bitmap
     * @param partName Multipart 폼 데이터의 파트 이름
     * @return Pair<생성된 임시 파일, MultipartBody.Part>
     */
    private fun createMultipartBodyPart(
        image: Bitmap,
        partName: String,
    ): Pair<File, MultipartBody.Part> {
        val extension = "jpg"
        val mimeType = "image/jpeg"

        // 캐시 디렉터리에 임시 파일 생성
        val tempFile = File(context.cacheDir, "finish_map_${System.currentTimeMillis()}.$extension")

        // Bitmap을 임시 파일로 압축하여 저장 (JPEG, 품질 90)
        try {
            FileOutputStream(tempFile).use { outputStream ->
                image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
        } catch (e: Exception) {
            tempFile.delete() // 에러 발생 시 파일 삭제
            throw DoRunException.DataError("이미지 파일 변환 실패: ${e.message}")
        }

        // 임시 파일로 RequestBody 생성
        val requestBody = tempFile.asRequestBody(mimeType.toMediaType())

        //  MultipartBody.Part 생성
        val part = MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)

        return Pair(tempFile, part)
    }

    suspend fun getRunSessions(
        isSelfied: Boolean?,
        startDateTime: String?,
    ): BaseResponse<List<RunSessionListResponseDto>> = runningSessionServiceApi.getRunSessions(isSelfied, startDateTime)
}
