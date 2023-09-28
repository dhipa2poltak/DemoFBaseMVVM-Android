package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class GetStreamConfigsUseCaseImpl(
  private val appRepository: AppRepository
): GetStreamConfigsUseCase {

  override operator fun invoke(): Observable<String> {
    return appRepository.getStreamConfigs()
  }
}
