package com.dpm.sixpack.presentation.common.util.capture

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * 이미지 저장 포맷
 */
enum class ImageFormat(
    val mimeType: String,
    val extension: String,
    val compressFormat: Bitmap.CompressFormat,
) {
    /** PNG 무손실 포맷 (인스타그램 최고 품질) */
    PNG("image/png", "png", Bitmap.CompressFormat.PNG),

    /** JPEG 고품질 포맷 (용량 효율적) */
    JPEG("image/jpeg", "jpg", Bitmap.CompressFormat.JPEG),
}

/**
 * Bitmap을 갤러리에 저장하는 유틸리티
 */
object ImageSaver {
    private const val DIRECTORY_NAME = "Sixpack"

    /** JPEG 품질 (100 = 최고 품질, 인스타그램 권장) */
    private const val JPEG_QUALITY = 100

    /** PNG는 무손실이므로 quality 파라미터 무시 (0-100 모두 동일) */
    private const val PNG_QUALITY = 100

    /**
     * Bitmap을 갤러리에 고품질로 저장합니다.
     *
     * IO 작업이므로 suspend 함수로 구현되어 있으며,
     * Dispatchers.IO에서 실행됩니다.
     *
     * @param context Android Context
     * @param bitmap 저장할 Bitmap (ARGB_8888 권장)
     * @param fileName 저장할 파일 이름 (확장자 제외)
     * @param format 저장 포맷 (PNG: 무손실, JPEG: 고품질)
     * @return Result<Uri> - 성공 시 저장된 파일의 Uri, 실패 시 예외
     */
    suspend fun saveToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        format: ImageFormat = ImageFormat.JPEG,
    ): Result<Uri> =
        withContext(Dispatchers.IO) {
            try {
                val uri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        saveWithMediaStore(context, bitmap, fileName, format)
                    } else {
                        saveWithFileApi(context, bitmap, fileName, format)
                    }

                if (uri != null) {
                    val quality =
                        when (format) {
                            ImageFormat.PNG -> "PNG (무손실)"
                            ImageFormat.JPEG -> "JPEG (quality=$JPEG_QUALITY)"
                        }
                    Timber.d("Image saved successfully: $uri, format=$quality, size=${bitmap.width}x${bitmap.height}")
                    Result.success(uri)
                } else {
                    Timber.e("Failed to save image: Uri is null")
                    Result.failure(IllegalStateException("이미지 저장에 실패했습니다"))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save image")
                Result.failure(e)
            }
        }

    /**
     * Android 10 (API 29) 이상에서 MediaStore를 사용하여 저장
     *
     * Scoped Storage를 사용하여 권한 없이도 갤러리에 저장 가능합니다.
     */
    private fun saveWithMediaStore(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        format: ImageFormat,
    ): Uri? {
        val contentValues =
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.${format.extension}")
                put(MediaStore.MediaColumns.MIME_TYPE, format.mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$DIRECTORY_NAME")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                val quality =
                    when (format) {
                        ImageFormat.PNG -> PNG_QUALITY
                        ImageFormat.JPEG -> JPEG_QUALITY
                    }
                bitmap.compress(format.compressFormat, quality, outputStream)
            }

            // IS_PENDING을 0으로 설정하여 다른 앱에서도 접근 가능하게 함
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)
        }

        return uri
    }

    /**
     * Android 10 미만에서 File API를 사용하여 저장
     *
     * WRITE_EXTERNAL_STORAGE 권한이 필요합니다.
     */
    @Suppress("DEPRECATION")
    private fun saveWithFileApi(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        format: ImageFormat,
    ): Uri? {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val directory = File(picturesDir, DIRECTORY_NAME)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$fileName.${format.extension}")
        var outputStream: OutputStream? = null

        try {
            outputStream = FileOutputStream(file)
            val quality =
                when (format) {
                    ImageFormat.PNG -> PNG_QUALITY
                    ImageFormat.JPEG -> JPEG_QUALITY
                }
            bitmap.compress(format.compressFormat, quality, outputStream)
            outputStream.flush()

            // 미디어 스캔 트리거 (갤러리에 즉시 표시되도록)
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                file.name,
                null,
            )

            return Uri.fromFile(file)
        } finally {
            outputStream?.close()
        }
    }

    /**
     * 저장 가능 여부를 확인합니다.
     *
     * Android 10 이상에서는 항상 true를 반환하고,
     * Android 10 미만에서는 WRITE_EXTERNAL_STORAGE 권한을 체크합니다.
     *
     * @param context Android Context
     * @return 저장 가능 여부
     */
    fun canSaveToGallery(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상에서는 Scoped Storage를 사용하므로 권한 불필요
            return true
        }

        // Android 10 미만에서는 WRITE_EXTERNAL_STORAGE 권한 필요
        return context.checkCallingOrSelfPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}
