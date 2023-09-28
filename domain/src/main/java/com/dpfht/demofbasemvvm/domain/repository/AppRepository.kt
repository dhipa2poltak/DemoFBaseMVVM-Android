package com.dpfht.demofbasemvvm.domain.repository

import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import io.reactivex.rxjava3.core.Observable

interface AppRepository {

  suspend fun isLogin(): Result<Boolean>

  suspend fun logEvent(eventName: String, param: Map<String, String>): VoidResult

  suspend fun fetchConfigs(): VoidResult

  fun getStreamConfigs(): Observable<String>

  suspend fun signInWithGoogle(): VoidResult

  fun getStreamLoginState(): Observable<LoginState>

  suspend fun logout(): VoidResult

  suspend fun startPhoneNumberVerification(phoneNumber: String): VoidResult
  suspend fun verifyPhoneNumberWithCode(code: String): VoidResult
  suspend fun resendVerificationCode(): VoidResult

  suspend fun getUserProfile(): Result<UserProfileEntity>
  fun getStreamPushMessage(): Observable<PushMessageEntity>
  fun getStreamFCMToken(): Observable<String>
  suspend fun fetchFCMToken(): VoidResult
  suspend fun postFCMMessage(to: String, title: String, message: String): Result<PostFCMMessageDomain>

  fun getStreamFCMQuota(): Observable<Result<Int>>
  suspend fun fetchFCMQuota(): VoidResult
  suspend fun setFCMQuota(count: Int): VoidResult

  fun getStreamBookState(): Observable<BookState>
  suspend fun addBook(book: BookEntity): VoidResult
  suspend fun updateBook(book: BookEntity): VoidResult
  suspend fun deleteBook(book: BookEntity): VoidResult
  suspend fun getAllBooks(): VoidResult
  suspend fun getBook(bookId: String): VoidResult
}
