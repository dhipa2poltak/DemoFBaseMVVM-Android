package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class ResendVerificationCodeUseCaseImpl(
  private val appRepository: AppRepository
): ResendVerificationCodeUseCase {

  override suspend operator fun invoke(): VoidResult {
    return appRepository.resendVerificationCode()
  }
}
