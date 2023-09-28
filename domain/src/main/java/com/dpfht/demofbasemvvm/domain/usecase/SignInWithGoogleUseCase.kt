package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface SignInWithGoogleUseCase {

  suspend operator fun invoke(): VoidResult
}
