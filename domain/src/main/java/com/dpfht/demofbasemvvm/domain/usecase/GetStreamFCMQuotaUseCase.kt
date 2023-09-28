package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.Result
import io.reactivex.rxjava3.core.Observable

interface GetStreamFCMQuotaUseCase {

  operator fun invoke(): Observable<Result<Int>>
}
