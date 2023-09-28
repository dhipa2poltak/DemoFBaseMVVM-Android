package com.dpfht.demofbasemvvm.domain.usecase

import io.reactivex.rxjava3.core.Observable

interface GetStreamFCMTokenUseCase {

  operator fun invoke(): Observable<String>
}
