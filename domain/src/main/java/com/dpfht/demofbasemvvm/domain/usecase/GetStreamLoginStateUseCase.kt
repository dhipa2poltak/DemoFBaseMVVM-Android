package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.LoginState
import io.reactivex.rxjava3.core.Observable

interface GetStreamLoginStateUseCase {

  operator fun invoke(): Observable<LoginState>
}
