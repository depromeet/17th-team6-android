package com.dpm.sixpack.domain.example.usecase

import com.dpm.sixpack.domain.example.ExampleRepository
import javax.inject.Inject

class FetchCountUseCase
    @Inject
    constructor(
        private val repository: ExampleRepository,
    ) {
        operator fun invoke() = repository.getCount()
    }
