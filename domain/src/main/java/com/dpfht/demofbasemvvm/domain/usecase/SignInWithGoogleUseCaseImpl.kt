package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class SignInWithGoogleUseCaseImpl(
  private val appRepository: AppRepository
): SignInWithGoogleUseCase {

  override suspend operator fun invoke(): VoidResult {
    return try {
      appRepository.signInWithGoogle()

      VoidResult.Success
    } catch (e: AppException) {
      VoidResult.Error(e.message)
    }
  }
}
