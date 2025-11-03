package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.util.DoRunResult
import java.io.File

interface FileRepository {
    suspend fun convertUriToFile(
        uriString: String,
        fileName: String,
    ): DoRunResult<File>
}
