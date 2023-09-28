package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface GetBookUseCase {

  suspend operator fun invoke(bookId: String): VoidResult
}
