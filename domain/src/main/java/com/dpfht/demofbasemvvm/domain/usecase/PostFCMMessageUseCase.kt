package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.Result

interface PostFCMMessageUseCase {

  suspend operator fun invoke(to: String, title: String, message: String): Result<PostFCMMessageDomain>
}
