package com.dpfht.demofbasemvvm.framework.data.datasource.remote

import android.content.Context
import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.data.model.remote.response.PostFCMMessageResponse
import com.dpfht.demofbasemvvm.data.model.remote.response.toDomain
import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.framework.R
import com.dpfht.demofbasemvvm.framework.data.datasource.remote.rest.RestService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.nio.charset.Charset

class RestDataSourceImpl(
  private val context: Context,
  private val restService: RestService
): RestDataSource {

  override suspend fun postFCMMessage(request: PostFCMMessageBodyRequest): PostFCMMessageDomain {
    return safeApiCall(Dispatchers.IO) { restService.postFCMMessage(request).toDomain() }
  }

  private suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): T {
    return withContext(dispatcher) {
      try {
        apiCall.invoke()
      } catch (t: Throwable) {
        throw when (t) {
          is IOException -> AppException(context.getString(R.string.framework_text_error_connection))
          is HttpException -> {
            val errorResponse = convertErrorBody(t)

            AppException(errorResponse?.results?.get(0)?.error ?: context.getString(R.string.framework_text_http_error))
          }
          else -> {
            AppException(context.getString(R.string.framework_text_error_conversion))
          }
        }
      }
    }
  }

  private fun convertErrorBody(t: HttpException): PostFCMMessageResponse? {
    return try {
      t.response()?.errorBody()?.source().let {
        val json = it?.readString(Charset.defaultCharset())
        val typeToken = object : TypeToken<PostFCMMessageResponse>() {}.type
        val errorResponse = Gson().fromJson<PostFCMMessageResponse>(json, typeToken)
        errorResponse
      }
    } catch (e: Exception) {
      null
    }
  }
}
