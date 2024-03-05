package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class AddBookUseCaseImpl(
  private val appRepository: AppRepository
): AddBookUseCase {

  override suspend operator fun invoke(book: BookEntity, uriStringImage: String): VoidResult {
    return try {
      appRepository.addBook(book, uriStringImage)

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
