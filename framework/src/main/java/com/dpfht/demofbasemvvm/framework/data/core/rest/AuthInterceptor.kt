package com.dpfht.demofbasemvvm.framework.data.core.rest

import com.dpfht.demofbasemvvm.framework.Constants
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class AuthInterceptor: Interceptor {
  override fun intercept(chain: Chain): Response {
    val requestBuilder = chain.request().newBuilder()
    requestBuilder.header("Authorization", Constants.RestApi.HEADER_AUTHORIZATION_VALUE)
    requestBuilder.header("Content-Type", "application/json")

    return chain.proceed(requestBuilder.build())
  }
}
