package com.dpfht.demofbasemvvm.framework.data.datasource.remote.rest

import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.data.model.remote.response.PostFCMMessageResponse
import com.dpfht.demofbasemvvm.framework.Constants
import retrofit2.http.Body
import retrofit2.http.POST

interface RestService {

  @POST(Constants.RestApi.PATH_FCM_SEND)
  suspend fun postFCMMessage(@Body request: PostFCMMessageBodyRequest): PostFCMMessageResponse
}
