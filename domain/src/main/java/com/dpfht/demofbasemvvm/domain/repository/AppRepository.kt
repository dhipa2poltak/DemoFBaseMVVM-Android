package com.dpfht.demofbasemvvm.domain.repository

import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaState
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.RemoteConfigEntity
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import io.reactivex.rxjava3.core.Observable

interface AppRepository {

  suspend fun isLogin(): Boolean

  suspend fun logEvent(eventName: String, param: Map<String, String>)

  suspend fun fetchConfigs()

  fun getStreamConfigs(): Observable<RemoteConfigEntity>

  suspend fun signInWithGoogle()

  fun getStreamLoginState(): Observable<LoginState>

  suspend fun logout()

  suspend fun startPhoneNumberVerification(phoneNumber: String)
  suspend fun verifyPhoneNumberWithCode(code: String)
  suspend fun resendVerificationCode()

  suspend fun getUserProfile(): UserProfileEntity
  fun getStreamPushMessage(): Observable<PushMessageEntity>
  fun getStreamFCMToken(): Observable<String>
  suspend fun fetchFCMToken()
  suspend fun postFCMMessage(to: String, title: String, message: String): PostFCMMessageDomain

  fun getStreamFCMQuota(): Observable<FCMQuotaState>
  suspend fun fetchFCMQuota()
  suspend fun setFCMQuota(count: Int)

  fun getStreamBookState(): Observable<BookState>
  suspend fun addBook(book: BookEntity, uriStringImage: String)
  suspend fun updateBook(book: BookEntity, uriStringImage: String)
  suspend fun deleteBook(book: BookEntity)
  suspend fun getAllBooks()
  suspend fun getBook(bookId: String)
}
