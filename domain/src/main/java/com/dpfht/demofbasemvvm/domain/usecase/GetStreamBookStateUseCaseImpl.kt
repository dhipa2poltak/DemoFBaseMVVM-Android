package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class GetStreamBookStateUseCaseImpl(
  private val appRepository: AppRepository
): GetStreamBookStateUseCase {

  override operator fun invoke(): Observable<BookState> {
    return appRepository.getStreamBookState()
  }
}
