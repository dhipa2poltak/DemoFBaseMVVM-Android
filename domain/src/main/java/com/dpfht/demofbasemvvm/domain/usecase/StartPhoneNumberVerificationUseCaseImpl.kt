package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class StartPhoneNumberVerificationUseCaseImpl(
  private val appRepository: AppRepository
): StartPhoneNumberVerificationUseCase {

  override suspend operator fun invoke(phoneNumber: String): VoidResult {
    return try {
      appRepository.startPhoneNumberVerification(phoneNumber)

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
