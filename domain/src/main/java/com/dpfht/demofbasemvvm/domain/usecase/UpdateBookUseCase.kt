package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface UpdateBookUseCase {

  suspend operator fun invoke(book: BookEntity, uriStringImage: String): VoidResult
}
