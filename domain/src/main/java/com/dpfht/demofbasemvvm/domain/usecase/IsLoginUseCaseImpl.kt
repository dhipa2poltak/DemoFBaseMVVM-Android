package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class IsLoginUseCaseImpl(
  private val appRepository: AppRepository
): IsLoginUseCase {

  override suspend operator fun invoke(): Result<Boolean> {
    return try {
      Result.Success(appRepository.isLogin())
    } catch (e: AppException) {
      Result.Error(e.message)
    }
  }
}
