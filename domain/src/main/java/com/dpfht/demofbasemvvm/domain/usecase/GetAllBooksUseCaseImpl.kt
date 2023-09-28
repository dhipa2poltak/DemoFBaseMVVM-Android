package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class GetAllBooksUseCaseImpl(
  private val appRepository: AppRepository
): GetAllBooksUseCase {

  override suspend operator fun invoke(): VoidResult {
    return appRepository.getAllBooks()
  }
}
