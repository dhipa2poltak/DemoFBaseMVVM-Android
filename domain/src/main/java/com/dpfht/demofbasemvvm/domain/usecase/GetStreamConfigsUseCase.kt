package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.RemoteConfigEntity
import io.reactivex.rxjava3.core.Observable

interface GetStreamConfigsUseCase {

  operator fun invoke(): Observable<RemoteConfigEntity>
}
