package com.dpfht.demofbasemvvm.datasource.remote

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import com.dpfht.demofbasemvvm.R
import com.dpfht.demofbasemvvm.data.datasource.FirebaseLoginDataSource
import com.dpfht.demofbasemvvm.domain.entity.AppException
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class FirebaseLoginDataSourceImpl(
  private val activity: AppCompatActivity
): FirebaseLoginDataSource {

  private val rawLoginState = PublishSubject.create<LoginState>()
  private val theLoginState: Observable<LoginState> = rawLoginState

  private val signInClient = Identity.getSignInClient(activity)

  private var verificationInProgress = false
  private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  private var storedVerificationId: String? = ""
  private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
  private var phoneNumber = ""

  init {
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
  }

  private val signInLauncher = activity.registerForActivityResult(
    StartIntentSenderForResult()
  ) { result ->
    handleSignInResult(result.data)
  }

  override suspend fun isLogin(): Boolean {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    return currentUser != null
  }

  override suspend fun getUserProfile(): UserProfileEntity {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    currentUser?.let {
      return UserProfileEntity(
        currentUser.uid,
        currentUser.displayName ?: "",
        currentUser.email ?: "",
        currentUser.phoneNumber ?: "",
        currentUser.photoUrl.toString()
      )
    }

    throw AppException("can't get user profile")
  }

  override fun getStreamLoginState(): Observable<LoginState> {
    return theLoginState
  }

  override suspend fun signInWithGoogle() {
    this.phoneNumber = ""

    val signInRequest = GetSignInIntentRequest.builder()
      .setServerClientId(activity.getString(R.string.default_web_client_id))
      .build()

    signInClient.getSignInIntent(signInRequest)
      .addOnSuccessListener { pendingIntent ->
        launchSignIn(pendingIntent)
      }
      .addOnFailureListener { e ->
        //Log.e("DemoFirebase", "Google Sign-in failed", e)
        rawLoginState.onNext(LoginState.Error("Google Sign-in failed $e"))
      }
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

  override suspend fun logout() {
    Firebase.auth.signOut()

    try {
      // Google sign out
      signInClient.signOut()
    } catch (_: Exception) {
    }

    rawLoginState.onNext(LoginState.Logout)
    reset()
  }

  override suspend fun startPhoneNumberVerification(phoneNumber: String) {
    this.phoneNumber = phoneNumber

    val auth = Firebase.auth
    val options = PhoneAuthOptions.newBuilder(auth)
      .setPhoneNumber(phoneNumber) // Phone number to verify
      .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
      .setActivity(activity) // Activity (for callback binding)
      .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
      .build()
    PhoneAuthProvider.verifyPhoneNumber(options)

    verificationInProgress = true
  }

  override suspend fun verifyPhoneNumberWithCode(code: String) {
    val verificationId = storedVerificationId
    verificationId?.let {
      val credential = PhoneAuthProvider.getCredential(verificationId, code)
      signInWithPhoneAuthCredential(credential)
    }
  }

  override suspend fun resendVerificationCode() {
    val token = resendToken
    val auth = Firebase.auth
    val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
      .setPhoneNumber(phoneNumber) // Phone number to verify
      .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
      .setActivity(activity) // Activity (for callback binding)
      .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks

    //if (token != null) {
    optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
    //}
    PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
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

  private fun reset() {
    rawLoginState.onNext(LoginState.None)

    verificationInProgress = false
    storedVerificationId = ""
    phoneNumber = ""
  }
}
