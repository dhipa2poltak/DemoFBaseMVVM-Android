package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaState
import io.reactivex.rxjava3.core.Observable

interface GetStreamFCMQuotaUseCase {

  operator fun invoke(): Observable<FCMQuotaState>
}
