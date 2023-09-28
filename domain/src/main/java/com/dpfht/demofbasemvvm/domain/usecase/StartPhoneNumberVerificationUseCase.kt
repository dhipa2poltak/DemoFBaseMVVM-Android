package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface StartPhoneNumberVerificationUseCase {

  suspend operator fun invoke(phoneNumber: String): VoidResult
}
