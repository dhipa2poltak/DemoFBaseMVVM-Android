package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class GetBookUseCaseImpl(
  private val appRepository: AppRepository
): GetBookUseCase {

  override suspend operator fun invoke(bookId: String): VoidResult {
    return appRepository.getBook(bookId)
  }
}