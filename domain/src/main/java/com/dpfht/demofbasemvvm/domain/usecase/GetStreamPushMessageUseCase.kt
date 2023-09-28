package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import io.reactivex.rxjava3.core.Observable

interface GetStreamPushMessageUseCase {

  operator fun invoke(): Observable<PushMessageEntity>
}
