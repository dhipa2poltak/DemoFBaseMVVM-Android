package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class StartPhoneNumberVerificationUseCaseImpl(
  private val appRepository: AppRepository
): StartPhoneNumberVerificationUseCase {

  override suspend operator fun invoke(phoneNumber: String): VoidResult {
    return appRepository.startPhoneNumberVerification(phoneNumber)
  }
}
