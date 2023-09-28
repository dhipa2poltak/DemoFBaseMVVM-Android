package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface FetchConfigsUseCase {

  suspend operator fun invoke(): VoidResult
}
