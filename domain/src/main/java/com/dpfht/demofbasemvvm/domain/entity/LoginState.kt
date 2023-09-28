package com.dpfht.demofbasemvvm.domain.entity

sealed class LoginState {
  object None: LoginState()
  object Login: LoginState()
  object Logout: LoginState()
  object VerificationCodeSent: LoginState()
  data class Error(val message: String): LoginState()
}