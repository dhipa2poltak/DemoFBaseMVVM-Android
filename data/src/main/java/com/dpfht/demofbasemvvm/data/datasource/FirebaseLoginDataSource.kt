package com.dpfht.demofbasemvvm.data.datasource

import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import io.reactivex.rxjava3.core.Observable

interface FirebaseLoginDataSource {

  suspend fun isLogin(): Boolean
  suspend fun getUserProfile(): UserProfileEntity
  fun getStreamLoginState(): Observable<LoginState>
  suspend fun signInWithGoogle()
  suspend fun startPhoneNumberVerification(phoneNumber: String)
  suspend fun verifyPhoneNumberWithCode(code: String)
  suspend fun resendVerificationCode()
  suspend fun logout()
}
