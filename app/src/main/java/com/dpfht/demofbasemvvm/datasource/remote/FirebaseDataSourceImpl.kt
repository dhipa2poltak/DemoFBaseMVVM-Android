package com.dpfht.demofbasemvvm.datasource.remote

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dpfht.demofbasemvvm.MainActivity
import com.dpfht.demofbasemvvm.R
import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.FCMQuotaEntity
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.PushMessageEntity
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.framework.Constants
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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
import java.util.concurrent.TimeUnit

class FirebaseDataSourceImpl(
  private val context: Context
): FirebaseDataSource {

  private val rawConfigs = BehaviorSubject.createDefault("")
  private val theConfigs: Observable<String> = rawConfigs

  private val rawLoginState = PublishSubject.create<LoginState>()
  private val theLoginState: Observable<LoginState> = rawLoginState

  private val rawReceivedPushMessage = PublishSubject.create<PushMessageEntity>()
  private val theReceivedPushMessage: Observable<PushMessageEntity> = rawReceivedPushMessage

  private val rawFCMToken = PublishSubject.create<String>()
  private val theFCMToken: Observable<String> = rawFCMToken

  private val rawStateErrorConfigs = PublishSubject.create<String>()
  //private val theStateErrorConfigs: Observable<String> = rawStateErrorConfigs

  private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

  private val rawFCMQuota = PublishSubject.create<Result<Int>>()
  private val theFCMQuota: Observable<Result<Int>> = rawFCMQuota

  private val rawBookState = PublishSubject.create<BookState>()
  private val theBookState: Observable<BookState> = rawBookState

  private val signInClient: SignInClient

  private val signInLauncher = MainActivity.instance.registerForActivityResult(StartIntentSenderForResult()) { result ->
    handleSignInResult(result.data)
  }

  private var verificationInProgress = false
  private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  private var storedVerificationId: String? = ""
  private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
  private var phoneNumber = ""

  init {
    val configSettings = remoteConfigSettings {
      minimumFetchIntervalInSeconds = 3600
    }
    remoteConfig.setConfigSettingsAsync(configSettings)
    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
      override fun onUpdate(configUpdate: ConfigUpdate) {
        //Log.d("Demo Firebase", "Updated keys: " + configUpdate.updatedKeys)

        if (configUpdate.updatedKeys.contains(Constants.Configs.KEY_CONFIG_TITLE_LOGIN_SCREEN)) {
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

    signInClient = Identity.getSignInClient(context)

    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

      override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        //Log.d("Demo Firebase", "onVerificationCompleted:$credential")
        verificationInProgress = false

        signInWithPhoneAuthCredential(credential)
      }

      override fun onVerificationFailed(e: FirebaseException) {
        //Log.w("Demo Firebase", "onVerificationFailed", e)
        verificationInProgress = false

        if (e is FirebaseAuthInvalidCredentialsException) {
          rawLoginState.onNext(LoginState.Error("Invalid phone number."))
        } else if (e is FirebaseTooManyRequestsException) {
          // The SMS quota for the project has been exceeded
          rawLoginState.onNext(LoginState.Error("Quota exceeded."))
        }

        rawLoginState.onNext(LoginState.Error("Verification Failed: ${e.message}"))
      }

      override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        //Log.d("Demo Firebase", "onCodeSent:$verificationId")

        storedVerificationId = verificationId
        resendToken = token

        rawLoginState.onNext(LoginState.VerificationCodeSent)
      }
    }

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
    val titleScreen = remoteConfig.getString(Constants.Configs.KEY_CONFIG_TITLE_LOGIN_SCREEN)
    rawConfigs.onNext(titleScreen)
  }

  override suspend fun isLogin(): Result<Boolean> {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    return Result.Success(currentUser != null)
  }

  override suspend fun logEvent(eventName: String, param: Map<String, String>): VoidResult {
    val bundle = Bundle()

    for (key in param.keys) {
      bundle.putString(key, param[key])
    }

    val firebaseAnalytics = Firebase.analytics
    firebaseAnalytics.logEvent(eventName, bundle)

    return VoidResult.Success
  }

  override suspend fun fetchConfigs(): VoidResult {
    remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        rawStateErrorConfigs.onNext("failed to fetch remote config")
      }

      emitConfigs()
    }

    return VoidResult.Success
  }

  override fun getStreamConfigs(): Observable<String> {
    return theConfigs
  }

  override fun getStreamLoginState(): Observable<LoginState> {
    return theLoginState
  }

  override suspend fun signInWithGoogle(): VoidResult {
    this.phoneNumber = ""

    val signInRequest = GetSignInIntentRequest.builder()
      .setServerClientId(context.getString(R.string.default_web_client_id))
      .build()

    signInClient.getSignInIntent(signInRequest)
      .addOnSuccessListener { pendingIntent ->
        launchSignIn(pendingIntent)
      }
      .addOnFailureListener { e ->
        //Log.e("DemoFirebase", "Google Sign-in failed", e)
        rawLoginState.onNext(LoginState.Error("Google Sign-in failed $e"))
      }

    return VoidResult.Success
  }

  private fun launchSignIn(pendingIntent: PendingIntent) {
    try {
      val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
      signInLauncher.launch(intentSenderRequest)
    } catch (e: IntentSender.SendIntentException) {
      //Log.e("DemoFirebase", "Couldn't start Sign In: ${e.localizedMessage}")
      rawLoginState.onNext(LoginState.Error("Couldn't start Sign In: ${e.localizedMessage}"))
    }
  }

  private fun handleSignInResult(data: Intent?) {
    try {
      val credential = signInClient.getSignInCredentialFromIntent(data)
      val idToken = credential.googleIdToken
      if (idToken != null) {
        //Log.d("Demo Firebase", "firebaseAuthWithGoogle: ${credential.id}")
        firebaseAuthWithGoogle(idToken)
      } else {
        // Shouldn't happen.
        //Log.d("DemoFirebase", "No ID token!")
        rawLoginState.onNext(LoginState.Error("No ID token!"))
      }
    } catch (e: ApiException) {
      // Google Sign In failed, update UI appropriately
      //Log.w("DemoFirebae", "Google sign in failed", e)
      rawLoginState.onNext(LoginState.Error("Google sign in failed $e"))
    }
  }

  private fun firebaseAuthWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val auth = Firebase.auth
    auth.signInWithCredential(credential).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        //Log.d("DemoFirebase", "signInWithCredential:success")

        rawLoginState.onNext(LoginState.Login)
      } else {
        //Log.w("DemoFirebase", "signInWithCredential:failure", task.exception)
        rawLoginState.onNext(LoginState.Error("signInWithCredential:failure ${task.exception}"))
      }
    }
  }

  override suspend fun logout(): VoidResult {
    Firebase.auth.signOut()

    try {
      // Google sign out
      signInClient.signOut()
    } catch (_: Exception) {
    }

    FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
      rawLoginState.onNext(LoginState.Logout)
      reset()
    }.addOnFailureListener {
      rawLoginState.onNext(LoginState.Logout)
      reset()
    }

    return VoidResult.Success
  }

  override suspend fun startPhoneNumberVerification(phoneNumber: String): VoidResult {
    this.phoneNumber = phoneNumber

    val auth = Firebase.auth
    val options = PhoneAuthOptions.newBuilder(auth)
      .setPhoneNumber(phoneNumber) // Phone number to verify
      .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
      .setActivity(MainActivity.instance) // Activity (for callback binding)
      .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
      .build()
    PhoneAuthProvider.verifyPhoneNumber(options)

    verificationInProgress = true

    return VoidResult.Success
  }

  override suspend fun verifyPhoneNumberWithCode(code: String): VoidResult {
    val verificationId = storedVerificationId
    verificationId?.let {
      val credential = PhoneAuthProvider.getCredential(verificationId, code)
      signInWithPhoneAuthCredential(credential)
    }

    return VoidResult.Success
  }

  override suspend fun resendVerificationCode(): VoidResult {
    val token = resendToken
    val auth = Firebase.auth
    val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
      .setPhoneNumber(phoneNumber) // Phone number to verify
      .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
      .setActivity(MainActivity.instance) // Activity (for callback binding)
      .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks

    //if (token != null) {
      optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
    //}
    PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())

    return VoidResult.Success
  }

  private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
    val auth = Firebase.auth
    auth.signInWithCredential(credential).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Sign in success, update UI with the signed-in user's information
        //Log.d("Demo Firebase", "signInWithCredential:success")
        rawLoginState.onNext(LoginState.Login)
      } else {
        //Log.w("Demo Firebase", "signInWithCredential:failure", task.exception)
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
          // The verification code entered was invalid
          rawLoginState.onNext(LoginState.Error("Invalid code.1"))
        } else {
          rawLoginState.onNext(LoginState.Error("Authentication Failed."))
        }
      }
    }
  }

  override suspend fun getUserProfile(): Result<UserProfileEntity> {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    currentUser?.let {
      return Result.Success(UserProfileEntity(
        currentUser.uid,
        currentUser.displayName ?: "",
        currentUser.email ?: "",
        currentUser.phoneNumber ?: "",
        currentUser.photoUrl.toString()
      ))
    }

    return Result.ErrorResult("can't get user profile")
  }

  override fun getStreamPushMessage(): Observable<PushMessageEntity> {
    return theReceivedPushMessage
  }

  override fun getStreamFCMToken(): Observable<String> {
    return theFCMToken
  }

  override suspend fun fetchFCMToken(): VoidResult {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        return@addOnCompleteListener
      }

      val token = task.result
      rawFCMToken.onNext(token)
    }

    return VoidResult.Success
  }

  override fun getStreamFCMQuota(): Observable<Result<Int>> {
    return theFCMQuota
  }

  override suspend fun fetchFCMQuota(): VoidResult {
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
                rawFCMQuota.onNext(Result.Success(Constants.FCM.MAX_QUOTA_PER_DAY))
              } else if (todayTimeInMillis == savedTimeInMillis) {
                rawFCMQuota.onNext(Result.Success(fcmQuota.quota))
              } else {
                rawFCMQuota.onNext(Result.Success(0))
              }

              return@addOnSuccessListener
            }
          }
        }

        rawFCMQuota.onNext(Result.Success(Constants.FCM.MAX_QUOTA_PER_DAY))
      }.addOnFailureListener { e ->
        rawFCMQuota.onNext(Result.ErrorResult(e.message ?: "failed to fetch FCM Quota"))
        rawFCMQuota.onNext(Result.Success(0))
      }

      return VoidResult.Success
    }

    rawFCMQuota.onNext(Result.Success(0))
    return VoidResult.Error("failed to fetch FCM Quota because no uid")
  }

  override suspend fun setFCMQuota(count: Int): VoidResult {
    val uid = Firebase.auth.currentUser?.uid
    if (uid != null) {
      val formatterDate = SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN)
      val strNow = formatterDate.format(Date())
      val entity = FCMQuotaEntity(strNow, count)

      Firebase.firestore.collection("fcm_quotas").document(uid).set(entity).addOnSuccessListener {
        rawFCMQuota.onNext(Result.Success(count))
      }.addOnFailureListener { e ->
        rawFCMQuota.onNext(Result.Success(count))
        rawFCMQuota.onNext(Result.ErrorResult(e.message ?: "failed to set FCM Quota"))
      }

      return VoidResult.Success
    }

    rawFCMQuota.onNext(Result.Success(count))
    return VoidResult.Error("failed to set FCM Quota because no uid")
  }

  override fun getStreamBookState(): Observable<BookState> {
    return theBookState
  }

  override suspend fun addBook(book: BookEntity, uriStringImage: String): VoidResult {
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

      return VoidResult.Success
    }

    return VoidResult.Error("failed to add book because no uid")
  }

  private fun getFileExtension(uri: Uri): String {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: ""
  }

  override suspend fun updateBook(book: BookEntity, uriStringImage: String): VoidResult {
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

    return VoidResult.Success
  }

  override suspend fun deleteBook(book: BookEntity): VoidResult {
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

    return VoidResult.Success
  }

  override suspend fun getAllBooks(): VoidResult {
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

      return VoidResult.Success
    }

    return VoidResult.Error("Failed to get all books because no uid")
  }

  override suspend fun getBook(bookId: String): VoidResult {
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

      return VoidResult.Success
    }

    return VoidResult.Error("Failed to get book data because no uid")
  }

  private fun reset() {
    //rawConfigs.onNext("")
    rawLoginState.onNext(LoginState.None)
    rawReceivedPushMessage.onNext(PushMessageEntity())
    rawFCMToken.onNext("")
    rawStateErrorConfigs.onNext("")
    rawFCMQuota.onNext(Result.Success(-1))
    rawBookState.onNext(BookState.None)

    verificationInProgress = false
    storedVerificationId = ""
    phoneNumber = ""
  }
}
