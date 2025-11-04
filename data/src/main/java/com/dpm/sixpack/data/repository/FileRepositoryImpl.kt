package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.file.api.FileDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.repository.FileRepository
import com.dpm.sixpack.domain.util.DoRunResult
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileDataSource: FileDataSource,
) : FileRepository {
    override suspend fun convertUriToFile(
        uriString: String,
        fileName: String,
    ): DoRunResult<File> =
        try {
            val file = fileDataSource.convertUriToFile(uriString, fileName)
            DoRunResult.Success(file)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert Uri to File: $uriString")
            DoRunResult.Failure(
                DoRunException.DataError(
                    message = "파일 변환에 실패했습니다",
                    cause = e,
                ),
            )
        }
}
