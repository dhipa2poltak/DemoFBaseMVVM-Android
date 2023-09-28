package com.dpfht.demofbasemvvm.domain.entity

data class PostFCMMessageDomain(
  val multicastId: String = "",
  val success: Int = 0,
  val failure: Int = 0,
  val canonicalIds: Int = 0,
  val results: List<ResultEntity> = listOf()
)

data class ResultEntity(
  val messageId: String = "",
  val error: String = ""
)
