package com.dpm.sixpack.data.source.local.file.api

import java.io.File

interface FileDataSource {
    suspend fun convertUriToFile(
        uriString: String,
        fileName: String,
    ): File
}
