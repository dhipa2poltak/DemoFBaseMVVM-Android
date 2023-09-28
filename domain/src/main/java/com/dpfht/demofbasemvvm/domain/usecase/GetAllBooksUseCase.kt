package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface GetAllBooksUseCase {

  suspend operator fun invoke(): VoidResult
}
