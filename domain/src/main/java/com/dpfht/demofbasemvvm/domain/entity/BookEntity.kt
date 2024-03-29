package com.dpfht.demofbasemvvm.domain.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class BookEntity(
  val documentId: String = "",
  val title: String = "",
  val writer: String = "",
  val description: String = "",
  val stock: Int = 0,
  val pathImageInServer: String = "",
  val urlImage: String = "",
  val uid: String = "",
  val createdAt: Long = 0
): Serializable
