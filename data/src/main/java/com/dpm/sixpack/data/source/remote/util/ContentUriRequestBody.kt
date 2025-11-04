package com.dpm.sixpack.data.source.remote.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns.DISPLAY_NAME
import android.provider.OpenableColumns.SIZE
import android.util.Size
import androidx.annotation.RequiresApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayOutputStream

/**
 * URI에서 이미지를 읽어 자동으로 압축하는 RequestBody
 *
 * 기능:
 * - 이미지 자동 압축 (최대 1024x1024)
 * - 파일 크기에 따라 JPEG 품질 자동 조정
 * - 네트워크 트래픽 감소
 *
 * @param contentResolver ContentResolver
 * @param uri 이미지 URI
 */
@RequiresApi(Build.VERSION_CODES.P)
class ContentUriRequestBody(
    private val contentResolver: ContentResolver,
    private val uri: Uri,
) : RequestBody() {
    private var fileName = ""
    private var size = -1L
    private var compressedImage: ByteArray? = null

    init {
        getImageMetaData()
        compressImage()
    }

    private fun getImageMetaData() =
        runCatching {
            contentResolver
                .query(
                    uri,
                    arrayOf(DISPLAY_NAME, SIZE),
                    null,
                    null,
                    null,
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
                        size = cursor.getLong(cursor.getColumnIndexOrThrow(SIZE))
                    }
                }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun compressImage() =
        runCatching {
            val source = ImageDecoder.createSource(contentResolver, uri)
            val bitmap =
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true

                    val size = calculateTargetSize(info.size.width, info.size.height)
                    decoder.setTargetSize(size.width, size.height)
                }

            ByteArrayOutputStream().use { buffer ->
                val quality = calculateQuality(size)

                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, buffer)
                compressedImage = buffer.toByteArray()
                bitmap.recycle()
            }
        }

    private fun calculateTargetSize(
        width: Int,
        height: Int,
    ): Size {
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return Size(width, height)
        }

        val scaleFactor = (MAX_WIDTH / width.toFloat()).coerceAtMost(MAX_HEIGHT / height.toFloat())
        val targetWidth = (width * scaleFactor).toInt()
        val targetHeight = (height * scaleFactor).toInt()

        return Size(targetWidth, targetHeight)
    }

    private fun calculateQuality(fileSize: Long): Int =
        when {
            fileSize > 3 * MAX_FILE_SIZE -> 75
            fileSize > 1 * MAX_FILE_SIZE -> 80
            else -> 90
        }

    override fun contentLength(): Long = compressedImage?.size?.toLong() ?: -1L

    override fun contentType(): MediaType? =
        contentResolver.getType(uri)?.toMediaTypeOrNull() ?: DEFAULT_MIME_TYPE.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        compressedImage?.let(sink::write)
    }

    /**
     * MultipartBody.Part로 변환
     * @param name Multipart 파트 이름 (기본값: "selfieImage")
     */
    fun toFormData(name: String = IMAGE): MultipartBody.Part = MultipartBody.Part.createFormData(name, fileName, this)

    companion object {
        private const val IMAGE = "selfieImage"
        private const val DEFAULT_MIME_TYPE = "image/jpeg"
        private const val MAX_WIDTH = 1024
        private const val MAX_HEIGHT = 1024
        private const val MAX_FILE_SIZE = 1024 * 1024 // 1MB
    }
}
