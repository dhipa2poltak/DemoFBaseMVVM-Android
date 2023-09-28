package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class GetStreamLoginStateUseCaseImpl(
  private val appRepository: AppRepository
): GetStreamLoginStateUseCase {

  override operator fun invoke(): Observable<LoginState> {
    return appRepository.getStreamLoginState()
  }
}
