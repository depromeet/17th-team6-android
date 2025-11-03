package com.dpm.sixpack.data.source.local.file

import android.content.Context
import android.net.Uri
import com.dpm.sixpack.data.source.local.file.api.FileDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FileDataSource {
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

            inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            tempFile
        }
}
