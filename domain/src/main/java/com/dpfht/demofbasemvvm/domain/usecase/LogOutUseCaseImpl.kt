package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class LogOutUseCaseImpl(
  private val appRepository: AppRepository
): LogOutUseCase {

  override suspend operator fun invoke(): VoidResult {
    return appRepository.logout()
  }
}
