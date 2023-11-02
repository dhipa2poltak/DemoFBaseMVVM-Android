package com.dpfht.demofbasemvvm.framework.data.datasource.remote

import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.data.model.remote.response.toDomain
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.framework.data.datasource.remote.rest.RestService
import com.dpfht.demofbasemvvm.framework.data.datasource.remote.rest.safeApiCall
import kotlinx.coroutines.Dispatchers

class RestDataSourceImpl(
  private val restService: RestService
): RestDataSource {

  override suspend fun postFCMMessage(request: PostFCMMessageBodyRequest): Result<PostFCMMessageDomain> {
    return safeApiCall(Dispatchers.IO) { restService.postFCMMessage(request).toDomain() }
  }
}
