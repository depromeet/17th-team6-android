package com.dpm.sixpack.data.source.local.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.dpm.sixpack.data.source.local.file.api.FileDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.min

class FileDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FileDataSource {
    companion object {
        // 이미지 압축 설정
        private const val MAX_IMAGE_SIZE = 1920 // 최대 이미지 크기 (px)
        private const val COMPRESS_QUALITY = 90 // JPEG 압축 품질 (손실 최소화)
    }

    override suspend fun convertUriToFile(
        uriString: String,
        fileName: String,
    ): File =
        withContext(Dispatchers.IO) {
            val uri = Uri.parse(uriString)
            val inputStream =
                context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("Cannot open input stream from uri: $uriString")

            val tempFile = File(context.cacheDir, fileName)

            // 이미지 파일인지 확인하고 압축 처리
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType?.startsWith("image/") == true) {
                try {
                    // 이미지를 Bitmap으로 로드하고 압축
                    val compressedBitmap =
                        inputStream.use { input ->
                            compressImage(input.readBytes())
                        }

                    // 압축된 이미지를 파일로 저장
                    FileOutputStream(tempFile).use { output ->
                        compressedBitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            COMPRESS_QUALITY,
                            output,
                        )
                    }

                    compressedBitmap.recycle()
                    Timber
                        .d(
                            "Image compressed and saved: ${tempFile.absolutePath}, " +
                                "size: ${tempFile.length() / 1024}KB",
                        )
                } catch (e: Exception) {
                    Timber.w(e, "Failed to compress image, saving original")
                    // 압축 실패 시 원본 저장
                    inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            } else {
                // 이미지가 아닌 경우 원본 그대로 저장
                inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            tempFile
        }

    /**
     * 이미지를 압축하는 내부 함수
     * - 큰 이미지는 리사이징
     * - 메모리 효율적으로 처리
     */
    private fun compressImage(imageBytes: ByteArray): Bitmap {
        // 1. 이미지 크기 확인 (메모리에 로드하지 않고)
        val options =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        // 2. 샘플링 비율 계산 (리사이징이 필요한 경우)
        val scaleFactor = calculateScaleFactor(originalWidth, originalHeight)

        // 3. 리사이징된 Bitmap 로드
        val scaledOptions =
            BitmapFactory.Options().apply {
                inSampleSize = scaleFactor
                inJustDecodeBounds = false
            }

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, scaledOptions)

        // 4. 정확한 크기로 리사이징 (필요한 경우)
        return if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
            val scale =
                min(
                    MAX_IMAGE_SIZE.toFloat() / bitmap.width,
                    MAX_IMAGE_SIZE.toFloat() / bitmap.height,
                )
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            if (resizedBitmap != bitmap) {
                bitmap.recycle()
            }
            Timber.d("Image resized: ${originalWidth}x$originalHeight -> ${newWidth}x$newHeight")
            resizedBitmap
        } else {
            bitmap
        }
    }

    /**
     * inSampleSize 계산 (메모리 효율적인 로딩을 위한 샘플링 비율)
     */
    private fun calculateScaleFactor(
        width: Int,
        height: Int,
    ): Int {
        var inSampleSize = 1
        if (width > MAX_IMAGE_SIZE || height > MAX_IMAGE_SIZE) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while ((halfWidth / inSampleSize) >= MAX_IMAGE_SIZE && (halfHeight / inSampleSize) >= MAX_IMAGE_SIZE) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
