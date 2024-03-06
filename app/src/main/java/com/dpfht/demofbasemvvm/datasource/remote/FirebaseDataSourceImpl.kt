package com.dpfht.demofbasemvvm.datasource.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dpfht.demofbasemvvm.R
import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaEntity
import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaState
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.RemoteConfigEntity
import com.dpfht.demofbasemvvm.framework.Constants
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.ktx.storage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirebaseDataSourceImpl(
  private val context: Context
): FirebaseDataSource {

  private val rawConfigs = BehaviorSubject.create<RemoteConfigEntity>()
  private val theConfigs: Observable<RemoteConfigEntity> = rawConfigs

  private val rawReceivedPushMessage = PublishSubject.create<PushMessageEntity>()
  private val theReceivedPushMessage: Observable<PushMessageEntity> = rawReceivedPushMessage

  private val rawFCMToken = PublishSubject.create<String>()
  private val theFCMToken: Observable<String> = rawFCMToken

  private val rawStateErrorConfigs = PublishSubject.create<String>()
  //private val theStateErrorConfigs: Observable<String> = rawStateErrorConfigs

  private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

  private val rawFCMQuota = PublishSubject.create<FCMQuotaState>()
  private val theFCMQuota: Observable<FCMQuotaState> = rawFCMQuota

  private val rawBookState = PublishSubject.create<BookState>()
  private val theBookState: Observable<BookState> = rawBookState

  init {
    val configSettings = remoteConfigSettings {
      minimumFetchIntervalInSeconds = 3600
    }
    remoteConfig.setConfigSettingsAsync(configSettings)
    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
      override fun onUpdate(configUpdate: ConfigUpdate) {
        //Log.d("Demo Firebase", "Updated keys: " + configUpdate.updatedKeys)

        if (configUpdate.updatedKeys.contains(Constants.RemoteConfigs.KEY_CONFIG_TITLE_LOGIN_SCREEN)) {
          remoteConfig.activate().addOnCompleteListener {
            emitConfigs()
          }
        }
      }

      override fun onError(error: FirebaseRemoteConfigException) {
        //Log.w("Demo Firebase", "Config update error with code: " + error.code, error)
        rawStateErrorConfigs.onNext("Config update error with code: ${error.code}")
      }
    })

    LocalBroadcastManager.getInstance(context).registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
          val title = it.getStringExtra(Constants.ExtraParamName.EXTRA_TITLE) ?: ""
          val message = it.getStringExtra(Constants.ExtraParamName.EXTRA_MESSAGE) ?: ""

          rawReceivedPushMessage.onNext(PushMessageEntity(title, message))
        }
      }

    }, IntentFilter(Constants.BroadcastTagName.RECEIVED_PUSH_MESSAGE))

    LocalBroadcastManager.getInstance(context).registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        val fcmToken = intent?.getStringExtra(Constants.ExtraParamName.EXTRA_FCM_TOKEN) ?: ""

        rawFCMToken.onNext(fcmToken)
      }

    }, IntentFilter(Constants.BroadcastTagName.RECEIVED_NEW_FCM_TOKEN))
  }

  private fun emitConfigs() {
    val titleScreen = remoteConfig.getString(Constants.RemoteConfigs.KEY_CONFIG_TITLE_LOGIN_SCREEN)
    rawConfigs.onNext(RemoteConfigEntity(titleLoginScreen = titleScreen))
  }

  override suspend fun logEvent(eventName: String, param: Map<String, String>) {
    val bundle = Bundle()

    for (key in param.keys) {
      bundle.putString(key, param[key])
    }

    val firebaseAnalytics = Firebase.analytics
    firebaseAnalytics.logEvent(eventName, bundle)
  }

  override suspend fun fetchConfigs() {
    remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        rawStateErrorConfigs.onNext("failed to fetch remote config")
      }

      emitConfigs()
    }
  }

  override fun getStreamConfigs(): Observable<RemoteConfigEntity> {
    return theConfigs
  }

  override suspend fun logout() {
    FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
      reset()
    }.addOnFailureListener {
      reset()
    }
  }

  override fun getStreamPushMessage(): Observable<PushMessageEntity> {
    return theReceivedPushMessage
  }

  override fun getStreamFCMToken(): Observable<String> {
    return theFCMToken
  }

  override suspend fun fetchFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        return@addOnCompleteListener
      }

      val token = task.result
      rawFCMToken.onNext(token)
    }
  }

  override fun getStreamFCMQuota(): Observable<FCMQuotaState> {
    return theFCMQuota
  }

  override suspend fun fetchFCMQuota() {
    val uid = Firebase.auth.currentUser?.uid
    if (uid != null) {
      val docRef = Firebase.firestore.collection("fcm_quotas").document(uid)
      docRef.get().addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {
          val fcmQuota = documentSnapshot.toObject(FCMQuotaEntity::class.java)
          if (fcmQuota != null) {
            val formatterDate = SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN)
            val savedTimeInMillis = formatterDate.parse(fcmQuota.quotaDate)?.time

            val todayText = formatterDate.format(Calendar.getInstance().time)
            val todayTimeInMillis = formatterDate.parse(todayText)?.time

            if (todayTimeInMillis != null && savedTimeInMillis != null) {
              if (todayTimeInMillis > savedTimeInMillis) {
                rawFCMQuota.onNext(FCMQuotaState.QuotaCount(Constants.FCM.MAX_QUOTA_PER_DAY))
              } else if (todayTimeInMillis == savedTimeInMillis) {
                rawFCMQuota.onNext(FCMQuotaState.QuotaCount(fcmQuota.quota))
              } else {
                rawFCMQuota.onNext(FCMQuotaState.QuotaCount(0))
              }

              return@addOnSuccessListener
            }
          }
        }

        rawFCMQuota.onNext(FCMQuotaState.QuotaCount(Constants.FCM.MAX_QUOTA_PER_DAY))
      }.addOnFailureListener { _ ->
        //rawFCMQuota.onNext(0)
        //throw AppException("failed to fetch FCM Quota")
        rawFCMQuota.onNext(FCMQuotaState.Error("failed to fetch FCM Quota"))
      }

      return
    }

    rawFCMQuota.onNext(FCMQuotaState.QuotaCount(0))
    throw AppException("failed to fetch FCM Quota because no uid")
    //rawFCMQuota.onNext(FCMQuotaState.Error("failed to fetch FCM Quota because no uid"))
  }

  override suspend fun setFCMQuota(count: Int) {
    val uid = Firebase.auth.currentUser?.uid
    if (uid != null) {
      val formatterDate = SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN)
      val strNow = formatterDate.format(Date())
      val entity = FCMQuotaEntity(strNow, count)

      Firebase.firestore.collection("fcm_quotas").document(uid).set(entity).addOnSuccessListener {
        rawFCMQuota.onNext(FCMQuotaState.QuotaCount(count))
      }.addOnFailureListener { _ ->
        //rawFCMQuota.onNext(count)
        //throw AppException("failed to set FCM Quota")
        rawFCMQuota.onNext(FCMQuotaState.Error("failed to set FCM Quota"))
      }

      return
    }

    rawFCMQuota.onNext(FCMQuotaState.QuotaCount(count))
    throw AppException("failed to set FCM Quota because no uid")
    //rawFCMQuota.onNext(FCMQuotaState.Error("failed to set FCM Quota because no uid"))
  }

  override fun getStreamBookState(): Observable<BookState> {
    return theBookState
  }

  override suspend fun addBook(book: BookEntity, uriStringImage: String) {
    val uid = Firebase.auth.currentUser?.uid

    if (uid != null) {
      val docRef = Firebase.firestore.collection("books").document()
      val idDoc = docRef.id
      val createdAt = Calendar.getInstance().timeInMillis

      val uriImage = Uri.parse(uriStringImage)
      val ext = "." + getFileExtension(uriImage)
      val stream = context.contentResolver.openInputStream(uriImage)
      if (ext.isNotEmpty() && stream != null) {
        val pathInServer = "images/$uid/" + Calendar.getInstance().timeInMillis.toString() + ext

        val storageRef = Firebase.storage.reference.child(pathInServer)
        storageRef.putStream(stream).addOnSuccessListener {
          storageRef.downloadUrl.addOnSuccessListener {
            val newBook = book.copy(documentId = idDoc, uid = uid, createdAt = createdAt, pathImageInServer = pathInServer, urlImage = it.toString())
            docRef.set(newBook).addOnSuccessListener {
              rawBookState.onNext(BookState.BookAdded("the book is added successfully"))
            }.addOnFailureListener {
              rawBookState.onNext(BookState.ErrorAddBook("Failed to add the book"))
            }
          }.addOnFailureListener {
            rawBookState.onNext(BookState.ErrorAddBook("Failed to get image url, failed to add the book"))
          }
        }.addOnFailureListener {
          rawBookState.onNext(BookState.ErrorAddBook("Failed to upload image, failed to add the book"))
        }
      } else {
        rawBookState.onNext(BookState.ErrorAddBook("file extension is empty or stream is null, failed to add the book"))
      }

      return
    }

    throw AppException("failed to add book because no uid")
  }

  private fun getFileExtension(uri: Uri): String {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: ""
  }

  override suspend fun updateBook(book: BookEntity, uriStringImage: String) {
    if (uriStringImage.isNotEmpty()) {
      val uriImage = Uri.parse(uriStringImage)
      val stream = context.contentResolver.openInputStream(uriImage)
      if (stream != null) {
        val pathInServer = book.pathImageInServer

        val storageRef = Firebase.storage.reference.child(pathInServer)
        storageRef.putStream(stream).addOnSuccessListener {
          storageRef.downloadUrl.addOnSuccessListener {
            val docRef = Firebase.firestore.collection("books").document(book.documentId)
            val editingBook = book.copy(urlImage = it.toString())
            docRef.set(editingBook).addOnSuccessListener {
              rawBookState.onNext(BookState.BookUpdated("the book is updated successfully"))
            }.addOnFailureListener {
              rawBookState.onNext(BookState.ErrorUpdateBook("Failed to update the book"))
            }
          }.addOnFailureListener {
            rawBookState.onNext(BookState.ErrorUpdateBook("Failed to get image url"))

            val docRef = Firebase.firestore.collection("books").document(book.documentId)
            docRef.set(book).addOnSuccessListener {
              rawBookState.onNext(BookState.BookUpdated("the book is updated successfully"))
            }.addOnFailureListener {
              rawBookState.onNext(BookState.ErrorUpdateBook("Failed to update the book"))
            }
          }
        }.addOnFailureListener {
          rawBookState.onNext(BookState.ErrorUpdateBook("Failed to upload image"))

          val docRef = Firebase.firestore.collection("books").document(book.documentId)
          docRef.set(book).addOnSuccessListener {
            rawBookState.onNext(BookState.BookUpdated("the book is updated successfully"))
          }.addOnFailureListener {
            rawBookState.onNext(BookState.ErrorUpdateBook("Failed to update the book"))
          }
        }
      } else {
        rawBookState.onNext(BookState.ErrorUpdateBook("Failed to upload image, because stream is null"))

        val docRef = Firebase.firestore.collection("books").document(book.documentId)
        docRef.set(book).addOnSuccessListener {
          rawBookState.onNext(BookState.BookUpdated("the book is updated successfully"))
        }.addOnFailureListener {
          rawBookState.onNext(BookState.ErrorUpdateBook("Failed to update the book"))
        }
      }
    } else {
      val docRef = Firebase.firestore.collection("books").document(book.documentId)
      docRef.set(book).addOnSuccessListener {
        rawBookState.onNext(BookState.BookUpdated("the book is updated successfully"))
      }.addOnFailureListener {
        rawBookState.onNext(BookState.ErrorUpdateBook("Failed to update the book"))
      }
    }
  }

  override suspend fun deleteBook(book: BookEntity) {
    val pathInServer = book.pathImageInServer
    val storageRef = Firebase.storage.reference.child(pathInServer)
    storageRef.delete().addOnSuccessListener {
      val docRef = Firebase.firestore.collection("books").document(book.documentId)
      docRef.delete().addOnSuccessListener {
        rawBookState.onNext(BookState.BookDeleted("the book is deleted successfully"))
      }.addOnFailureListener {
        rawBookState.onNext(BookState.ErrorDeleteBook("Failed to delete the book"))
      }
    }.addOnFailureListener {
      rawBookState.onNext(BookState.ErrorDeleteBook("Failed to delete the book image"))

      val docRef = Firebase.firestore.collection("books").document(book.documentId)
      docRef.delete().addOnSuccessListener {
        rawBookState.onNext(BookState.BookDeleted("the book is deleted successfully"))
      }.addOnFailureListener {
        rawBookState.onNext(BookState.ErrorDeleteBook("Failed to delete the book"))
      }
    }
  }

  override suspend fun getAllBooks() {
    val uid = Firebase.auth.currentUser?.uid

    if (uid != null) {
      Firebase.firestore.collection("books")
        .whereEqualTo("uid", uid)
        .orderBy("createdAt")
        .get().addOnSuccessListener { qSnapshot ->

        val listOfBook = arrayListOf<BookEntity>()

        if (!qSnapshot.isEmpty) {
          for (d in qSnapshot.documents) {
            val book = d.toObject(BookEntity::class.java)
            book?.let {
              listOfBook.add(book)
            }
          }
        }

        rawBookState.onNext(BookState.BooksData(listOfBook.toList()))
      }.addOnFailureListener {
          rawBookState.onNext(BookState.ErrorGetAllBooks(it.message ?: "test"))
        //rawBookState.onNext(BookState.ErrorGetAllBooks("Failed to get all books data"))
      }

      return
    }

    throw AppException("Failed to get all books because no uid")
  }

  override suspend fun getBook(bookId: String) {
    val uid = Firebase.auth.currentUser?.uid

    if (uid != null) {
      Firebase.firestore.collection("books")
        .whereEqualTo("uid", uid)
        .whereEqualTo("documentId", bookId)
        .get()
        .addOnSuccessListener { qSnapshot ->


        if (!qSnapshot.isEmpty) {
          val d = qSnapshot.documents.first()
          val book = d.toObject(BookEntity::class.java)
          book?.let {
            rawBookState.onNext(BookState.BookData(it))
          }
        }
      }.addOnFailureListener {
        rawBookState.onNext(BookState.ErrorGetBook("Failed to get book data"))
      }

      return
    }

    throw AppException("Failed to get book data because no uid")
  }

  private fun reset() {
    rawReceivedPushMessage.onNext(PushMessageEntity())
    rawFCMToken.onNext("")
    rawStateErrorConfigs.onNext("")
    rawFCMQuota.onNext(FCMQuotaState.QuotaCount(-1))
    rawBookState.onNext(BookState.None)
    rawConfigs.onNext(RemoteConfigEntity())
  }
}
