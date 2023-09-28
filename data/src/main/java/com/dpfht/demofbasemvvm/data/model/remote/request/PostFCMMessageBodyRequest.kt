package com.dpfht.demofbasemvvm.data.model.remote.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
@Suppress("unused")
data class PostFCMMessageBodyRequest(
  val to: String = "",
  val data: Data? = null
)

@Keep
@Suppress("unused")
data class Data(
  val title: String = "",
  @SerializedName("body")
  val message: String = ""
)

