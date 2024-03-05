package com.dpfht.demofbasemvvm.domain.entity

sealed class FCMQuotaState {
  data class QuotaCount(val value: Int): FCMQuotaState()
  data class Error(val message: String): FCMQuotaState()
}
