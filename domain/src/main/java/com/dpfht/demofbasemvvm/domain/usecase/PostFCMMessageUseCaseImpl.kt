package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.repository.AppRepository

class PostFCMMessageUseCaseImpl(
  private val appRepository: AppRepository
): PostFCMMessageUseCase {

  override suspend operator fun invoke(to: String, title: String, message: String): Result<PostFCMMessageDomain> {
    return try {
      Result.Success(appRepository.postFCMMessage(to, title, message))
    } catch (e: AppException) {
      Result.Error(e.message)
    }
  }
}
