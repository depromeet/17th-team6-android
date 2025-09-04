package com.dpm.sixpack.domain.example.usecase

import com.dpm.sixpack.domain.example.ExampleRepository
import javax.inject.Inject

class ChangeCountUseCase
    @Inject
    constructor(
        private val repository: ExampleRepository,
    ) {
        suspend operator fun invoke(amount: Int) {
            if (amount == 0) return
            repository.changeCount(amount)
        }
    }
