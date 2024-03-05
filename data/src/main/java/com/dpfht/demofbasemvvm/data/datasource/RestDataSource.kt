package com.dpfht.demofbasemvvm.data.datasource

import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain

interface RestDataSource {

  suspend fun postFCMMessage(request: PostFCMMessageBodyRequest): PostFCMMessageDomain
}
