package com.dpfht.demofbasemvvm.data.model.remote.response

import androidx.annotation.Keep
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.ResultEntity
import com.google.gson.annotations.SerializedName

@Keep
@Suppress("unused")
data class PostFCMMessageResponse(
  @SerializedName("multicast_id")
  val multicastId: String? = "",
  val success: Int? = 0,
  val failure: Int? = 0,
  @SerializedName("canonical_ids")
  val canonicalIds: Int? = 0,
  val results: List<Result>? = listOf()
)

@Keep
@Suppress("unused")
data class Result(
  @SerializedName("message_id")
  val messageId: String? = "",
  val error: String? = ""
)

fun PostFCMMessageResponse.toDomain(): PostFCMMessageDomain {
  return PostFCMMessageDomain(
    multicastId = this.multicastId ?: "",
    success = this.success ?: 0,
    failure = this.failure ?: 0,
    canonicalIds = this.canonicalIds ?: 0,
    results = this.results?.map { it.toDomain() } ?: listOf()
  )
}

fun Result.toDomain(): ResultEntity {
  return ResultEntity(
    messageId = this.messageId ?: "",
    error = this.error ?: ""
  )
}
