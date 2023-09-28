package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class FetchFCMTokenUseCaseImpl(
  private val appRepository: AppRepository
): FetchFCMTokenUseCase {

  override suspend operator fun invoke(): VoidResult {
    return appRepository.fetchFCMToken()
  }
}
