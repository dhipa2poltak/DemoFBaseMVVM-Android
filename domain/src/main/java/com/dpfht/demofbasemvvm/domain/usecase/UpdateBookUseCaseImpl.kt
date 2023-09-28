package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class UpdateBookUseCaseImpl(
  private val appRepository: AppRepository
): UpdateBookUseCase {

  override suspend operator fun invoke(book: BookEntity): VoidResult {
    return appRepository.updateBook(book)
  }
}
