package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.FileRepository
import com.dpm.sixpack.domain.util.DoRunResult
import java.io.File
import javax.inject.Inject

class ConvertUriToFileUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    suspend operator fun invoke(
        uriString: String,
        fileName: String = "profile_image_${System.currentTimeMillis()}.jpg",
    ): DoRunResult<File> = fileRepository.convertUriToFile(uriString, fileName)
}
