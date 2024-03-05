package com.dpfht.demofbasemvvm.framework.data.datasource.remote.rest

import com.dpfht.demofbasemvvm.data.model.remote.response.PostFCMMessageResponse
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.Result.Error
import com.dpfht.demofbasemvvm.domain.entity.Result.Success
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.nio.charset.Charset

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): Result<T> {
  return withContext(dispatcher) {
    try {
      Success(apiCall.invoke())
    } catch (t: Throwable) {
      when (t) {
        is IOException -> Error("error in connection")
        is HttpException -> {
          //val code = t.code()
          val errorResponse = convertErrorBody(t)

          Error(errorResponse?.results?.get(0)?.error ?: "http error")
        }
        else -> {
          Error("error in conversion")
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
