package com.dpfht.demofbasemvvm.domain.entity

data class UserProfileEntity(
  val uid: String = "",
  val displayName: String = "",
  val email: String = "",
  val phoneNumber: String = "",
  val photoUrl: String = ""
)
