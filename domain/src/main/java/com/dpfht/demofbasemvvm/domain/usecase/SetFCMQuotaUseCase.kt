package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface SetFCMQuotaUseCase {

  suspend operator fun invoke(count: Int): VoidResult
}
