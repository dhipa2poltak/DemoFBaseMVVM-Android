package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity

interface GetUserProfileUseCase {

  suspend operator fun invoke(): Result<UserProfileEntity>
}
