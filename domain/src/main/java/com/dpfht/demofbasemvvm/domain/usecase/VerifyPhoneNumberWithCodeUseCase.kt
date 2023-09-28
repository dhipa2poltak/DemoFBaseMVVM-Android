package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface VerifyPhoneNumberWithCodeUseCase {

  suspend operator fun invoke(code: String): VoidResult
}
