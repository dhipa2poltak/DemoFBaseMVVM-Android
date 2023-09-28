package com.dpfht.demofbasemvvm.domain.usecase

import io.reactivex.rxjava3.core.Observable

interface GetStreamConfigsUseCase {

  operator fun invoke(): Observable<String>
}
