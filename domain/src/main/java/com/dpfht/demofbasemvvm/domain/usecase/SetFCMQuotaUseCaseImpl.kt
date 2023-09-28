package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class SetFCMQuotaUseCaseImpl(
  private val appRepository: AppRepository
): SetFCMQuotaUseCase {

  override suspend operator fun invoke(count: Int): VoidResult {
    return appRepository.setFCMQuota(count)
  }
}
