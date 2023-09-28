package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.Result

interface IsLoginUseCase {

  suspend operator fun invoke(): Result<Boolean>
}
