package com.dpfht.demofbasemvvm.data.repository

import com.dpfht.demofbasemvvm.data.datasource.FirebaseLoginDataSource
import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.data.model.remote.request.Data
import com.dpfht.demofbasemvvm.data.model.remote.request.PostFCMMessageBodyRequest
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaState
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.PostFCMMessageDomain
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.RemoteConfigEntity
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import io.reactivex.rxjava3.core.Observable

class AppRepositoryImpl(
  private val firebaseDataSource: FirebaseDataSource,
  private val restDataSource: RestDataSource
): AppRepository {

  lateinit var firebaseLoginDataSource: FirebaseLoginDataSource

  override suspend fun isLogin(): Boolean {
    return firebaseLoginDataSource.isLogin()
  }

  override suspend fun logEvent(eventName: String, param: Map<String, String>) {
    return firebaseDataSource.logEvent(eventName, param)
  }

  override suspend fun fetchConfigs() {
    return firebaseDataSource.fetchConfigs()
  }

  override fun getStreamConfigs(): Observable<RemoteConfigEntity> {
    return firebaseDataSource.getStreamConfigs()
  }

  override suspend fun signInWithGoogle() {
    return firebaseLoginDataSource.signInWithGoogle()
  }

  override fun getStreamLoginState(): Observable<LoginState> {
    return firebaseLoginDataSource.getStreamLoginState()
  }

  override suspend fun logout() {
    firebaseLoginDataSource.logout()
    firebaseDataSource.logout()
  }

  override suspend fun startPhoneNumberVerification(phoneNumber: String) {
    return firebaseLoginDataSource.startPhoneNumberVerification(phoneNumber)
  }

  override suspend fun verifyPhoneNumberWithCode(code: String) {
    return firebaseLoginDataSource.verifyPhoneNumberWithCode(code)
  }

  override suspend fun resendVerificationCode() {
    return firebaseLoginDataSource.resendVerificationCode()
  }

  override suspend fun getUserProfile(): UserProfileEntity {
    return firebaseLoginDataSource.getUserProfile()
  }

  override fun getStreamPushMessage(): Observable<PushMessageEntity> {
    return firebaseDataSource.getStreamPushMessage()
  }

  override fun getStreamFCMToken(): Observable<String> {
    return firebaseDataSource.getStreamFCMToken()
  }

  override suspend fun fetchFCMToken() {
    return firebaseDataSource.fetchFCMToken()
  }

  override suspend fun postFCMMessage(to: String, title: String, message: String): PostFCMMessageDomain {
    return restDataSource.postFCMMessage(PostFCMMessageBodyRequest(to, Data(title, message)))
  }

  override fun getStreamFCMQuota(): Observable<FCMQuotaState> {
    return firebaseDataSource.getStreamFCMQuota()
  }

  override suspend fun fetchFCMQuota() {
    return firebaseDataSource.fetchFCMQuota()
  }

  override suspend fun setFCMQuota(count: Int) {
    return firebaseDataSource.setFCMQuota(count)
  }

  override fun getStreamBookState(): Observable<BookState> {
    return firebaseDataSource.getStreamBookState()
  }

  override suspend fun addBook(book: BookEntity, uriStringImage: String) {
    return firebaseDataSource.addBook(book, uriStringImage)
  }

  override suspend fun updateBook(book: BookEntity, uriStringImage: String) {
    return firebaseDataSource.updateBook(book, uriStringImage)
  }

  override suspend fun deleteBook(book: BookEntity) {
    return firebaseDataSource.deleteBook(book)
  }

  override suspend fun getAllBooks() {
    return firebaseDataSource.getAllBooks()
  }

  override suspend fun getBook(bookId: String) {
    return firebaseDataSource.getBook(bookId)
  }
}
