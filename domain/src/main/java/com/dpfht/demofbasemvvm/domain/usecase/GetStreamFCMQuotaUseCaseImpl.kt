package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class GetStreamFCMQuotaUseCaseImpl(
  private val appRepository: AppRepository
): GetStreamFCMQuotaUseCase {

  override operator fun invoke(): Observable<Result<Int>> {
    return appRepository.getStreamFCMQuota()
  }
}
