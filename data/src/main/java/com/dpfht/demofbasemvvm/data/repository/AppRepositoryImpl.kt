package com.dpfht.demofbasemvvm.data.repository

import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.data.model.remote.request.Data
import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class AppRepositoryImpl(
  private val firebaseDataSource: FirebaseDataSource,
  private val restDataSource: RestDataSource
): AppRepository {

  override suspend fun isLogin(): Result<Boolean> {
    return firebaseDataSource.isLogin()
  }

  override suspend fun logEvent(eventName: String, param: Map<String, String>): VoidResult {
    return firebaseDataSource.logEvent(eventName, param)
  }

  override suspend fun fetchConfigs(): VoidResult {
    return firebaseDataSource.fetchConfigs()
  }

  override fun getStreamConfigs(): Observable<String> {
    return firebaseDataSource.getStreamConfigs()
  }

  override suspend fun signInWithGoogle(): VoidResult {
    return firebaseDataSource.signInWithGoogle()
  }

  override fun getStreamLoginState(): Observable<LoginState> {
    return firebaseDataSource.getStreamLoginState()
  }

  override suspend fun logout(): VoidResult {
    return firebaseDataSource.logout()
  }

  override suspend fun startPhoneNumberVerification(phoneNumber: String): VoidResult {
    return firebaseDataSource.startPhoneNumberVerification(phoneNumber)
  }

  override suspend fun verifyPhoneNumberWithCode(code: String): VoidResult {
    return firebaseDataSource.verifyPhoneNumberWithCode(code)
  }

  override suspend fun resendVerificationCode(): VoidResult {
    return firebaseDataSource.resendVerificationCode()
  }

  override suspend fun getUserProfile(): Result<UserProfileEntity> {
    return firebaseDataSource.getUserProfile()
  }

  override fun getStreamPushMessage(): Observable<PushMessageEntity> {
    return firebaseDataSource.getStreamPushMessage()
  }

  override fun getStreamFCMToken(): Observable<String> {
    return firebaseDataSource.getStreamFCMToken()
  }

  override suspend fun fetchFCMToken(): VoidResult {
    return firebaseDataSource.fetchFCMToken()
  }

  override suspend fun postFCMMessage(to: String, title: String, message: String): Result<PostFCMMessageDomain> {
    return restDataSource.postFCMMessage(PostFCMMessageBodyRequest(to, Data(title, message)))
  }

  override fun getStreamFCMQuota(): Observable<Result<Int>> {
    return firebaseDataSource.getStreamFCMQuota()
  }

  override suspend fun fetchFCMQuota(): VoidResult {
    return firebaseDataSource.fetchFCMQuota()
  }

  override suspend fun setFCMQuota(count: Int): VoidResult {
    return firebaseDataSource.setFCMQuota(count)
  }

  override fun getStreamBookState(): Observable<BookState> {
    return firebaseDataSource.getStreamBookState()
  }

  override suspend fun addBook(book: BookEntity): VoidResult {
    return firebaseDataSource.addBook(book)
  }

  override suspend fun updateBook(book: BookEntity): VoidResult {
    return firebaseDataSource.updateBook(book)
  }

  override suspend fun deleteBook(book: BookEntity): VoidResult {
    return firebaseDataSource.deleteBook(book)
  }

  override suspend fun getAllBooks(): VoidResult {
    return firebaseDataSource.getAllBooks()
  }

  override suspend fun getBook(bookId: String): VoidResult {
    return firebaseDataSource.getBook(bookId)
  }
}
