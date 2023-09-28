package com.dpfht.demofbasemvvm.data.datasource

import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.Result

interface RestDataSource {

  suspend fun postFCMMessage(request: PostFCMMessageBodyRequest): Result<PostFCMMessageDomain>
}
