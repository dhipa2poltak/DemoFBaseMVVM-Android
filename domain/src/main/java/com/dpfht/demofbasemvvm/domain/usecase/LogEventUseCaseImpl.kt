package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class LogEventUseCaseImpl(
  private val appRepository: AppRepository
): LogEventUseCase {

  override suspend operator fun invoke(eventName: String, param: Map<String, String>): VoidResult {
    return try {
      appRepository.logEvent(eventName, param)

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
