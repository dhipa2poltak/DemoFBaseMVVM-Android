package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class GetStreamPushMessageUseCaseImpl(
  private val appRepository: AppRepository
): GetStreamPushMessageUseCase {

  override operator fun invoke(): Observable<PushMessageEntity> {
    return appRepository.getStreamPushMessage()
  }
}
