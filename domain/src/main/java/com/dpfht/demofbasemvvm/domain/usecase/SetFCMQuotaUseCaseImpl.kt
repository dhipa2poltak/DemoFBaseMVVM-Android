package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class SetFCMQuotaUseCaseImpl(
  private val appRepository: AppRepository
): SetFCMQuotaUseCase {

  override suspend operator fun invoke(count: Int): VoidResult {
    return try {
      appRepository.setFCMQuota(count)

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
