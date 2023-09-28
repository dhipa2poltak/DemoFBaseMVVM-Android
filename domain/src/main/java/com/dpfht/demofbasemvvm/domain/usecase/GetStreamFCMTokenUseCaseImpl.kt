package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class GetStreamFCMTokenUseCaseImpl(
  private val appRepository: AppRepository
): GetStreamFCMTokenUseCase {

  override operator fun invoke(): Observable<String> {
    return appRepository.getStreamFCMToken()
  }
}
