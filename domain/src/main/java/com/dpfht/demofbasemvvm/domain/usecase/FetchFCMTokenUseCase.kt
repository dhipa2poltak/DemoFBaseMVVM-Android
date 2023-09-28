package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface FetchFCMTokenUseCase {

  suspend operator fun invoke(): VoidResult
}
