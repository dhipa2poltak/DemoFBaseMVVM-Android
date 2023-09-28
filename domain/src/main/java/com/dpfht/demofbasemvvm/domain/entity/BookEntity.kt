package com.dpfht.demofbasemvvm.domain.entity

import java.io.Serializable

data class BookEntity(
  val documentId: String = "",
  val title: String = "",
  val writer: String = "",
  val description: String = "",
  val stock: Int = 0,
  val uriLocalPhoto: String = "",
  val urlRemotePhoto: String = "",
  val uid: String = "",
  val createdAt: Long = 0
): Serializable
