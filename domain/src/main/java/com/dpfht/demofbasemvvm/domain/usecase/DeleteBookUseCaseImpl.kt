package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class DeleteBookUseCaseImpl(
  private val appRepository: AppRepository
): DeleteBookUseCase {

  override suspend operator fun invoke(book: BookEntity): VoidResult {
    return try {
      appRepository.deleteBook(book)

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
