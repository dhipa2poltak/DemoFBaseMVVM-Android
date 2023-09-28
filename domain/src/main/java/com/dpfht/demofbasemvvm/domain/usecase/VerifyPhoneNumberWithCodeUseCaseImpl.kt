package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class VerifyPhoneNumberWithCodeUseCaseImpl(
  private val appRepository: AppRepository
): VerifyPhoneNumberWithCodeUseCase {

  override suspend operator fun invoke(code: String): VoidResult {
    return appRepository.verifyPhoneNumberWithCode(code)
  }
}
