package com.dpm.sixpack.data.source.remote.datasoruce

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
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
        mapImageUri: Uri,
    ): BaseResponse<FinishRunningResponseDto> {
        val dataRequestBody =
            Json
                .encodeToString(finishRequestDto.data) // DTO를 JSON 문자열로
                .toRequestBody("application/json".toMediaType())

        // Uri에서 임시 파일을 생성하고, MultipartBody.Part로 변환
        val (tempFile, mapImagePart) = createMultipartBodyPart(mapImageUri, "mapImage")

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

    private fun createMultipartBodyPart(
        uri: Uri,
        partName: String,
    ): Pair<File, MultipartBody.Part> {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

        // 캐시 디렉토리에 임시 파일 생성
        val tempFile = File.createTempFile("finish_map_", ".$extension", context.cacheDir)

        // Uri의 내용을 임시 파일로 복사
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
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
}
