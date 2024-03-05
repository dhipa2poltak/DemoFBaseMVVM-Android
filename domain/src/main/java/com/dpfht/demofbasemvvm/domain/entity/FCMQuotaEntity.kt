package com.dpfht.demofbasemvvm.domain.entity

import androidx.annotation.Keep

@Keep
data class FCMQuotaEntity(
  val quotaDate: String = "",
  val quota: Int = 0
)
