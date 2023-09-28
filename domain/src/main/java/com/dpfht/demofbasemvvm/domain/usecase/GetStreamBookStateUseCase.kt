package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.BookState
import io.reactivex.rxjava3.core.Observable

interface GetStreamBookStateUseCase {

  operator fun invoke(): Observable<BookState>
}