package com.dpfht.demofbasemvvm.data.datasource

import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaState
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.RemoteConfigEntity
import io.reactivex.rxjava3.core.Observable

interface FirebaseDataSource {
  suspend fun logEvent(eventName: String, param: Map<String, String>)
  suspend fun fetchConfigs()
  fun getStreamConfigs(): Observable<RemoteConfigEntity>
  suspend fun logout()
  fun getStreamPushMessage(): Observable<PushMessageEntity>
  fun getStreamFCMToken(): Observable<String>
  suspend fun fetchFCMToken()
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
