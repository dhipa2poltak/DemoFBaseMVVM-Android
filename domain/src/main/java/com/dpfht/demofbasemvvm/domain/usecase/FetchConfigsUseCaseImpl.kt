package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class FetchConfigsUseCaseImpl(
  private val appRepository: AppRepository
): FetchConfigsUseCase {

  override suspend operator fun invoke(): VoidResult {
    return appRepository.fetchConfigs()
  }
}
