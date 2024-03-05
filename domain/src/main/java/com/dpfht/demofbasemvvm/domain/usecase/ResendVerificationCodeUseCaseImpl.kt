package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class ResendVerificationCodeUseCaseImpl(
  private val appRepository: AppRepository
): ResendVerificationCodeUseCase {

  override suspend operator fun invoke(): VoidResult {
    return try {
      appRepository.resendVerificationCode()

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
