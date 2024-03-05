package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class GetUserProfileUseCaseImpl(
  private val appRepository: AppRepository
): GetUserProfileUseCase {

  override suspend operator fun invoke(): Result<UserProfileEntity> {
    return try {
      Result.Success(appRepository.getUserProfile())
    } catch (e: AppException) {
      Result.Error(e.message)
    }
  }
}
