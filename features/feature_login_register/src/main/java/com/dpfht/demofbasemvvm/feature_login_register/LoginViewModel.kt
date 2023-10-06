package com.dpfht.demofbasemvvm.feature_login_register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.usecase.FetchConfigsUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamConfigsUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamLoginStateUseCase
import com.dpfht.demofbasemvvm.domain.usecase.ResendVerificationCodeUseCase
import com.dpfht.demofbasemvvm.domain.usecase.SignInWithGoogleUseCase
import com.dpfht.demofbasemvvm.domain.usecase.StartPhoneNumberVerificationUseCase
import com.dpfht.demofbasemvvm.domain.usecase.VerifyPhoneNumberWithCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val getStreamConfigsUseCase: GetStreamConfigsUseCase,
  private val fetchConfigsUseCase: FetchConfigsUseCase,
  private val getStreamLoginStateUseCase: GetStreamLoginStateUseCase,
  private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
  private val startPhoneNumberVerificationUseCase: StartPhoneNumberVerificationUseCase,
  private val verifyPhoneNumberWithCodeUseCase: VerifyPhoneNumberWithCodeUseCase,
  private val resendVerificationCodeUseCase: ResendVerificationCodeUseCase
): ViewModel() {

  private val _isShowDialogLoading = MutableLiveData<Boolean>()
  val isShowDialogLoading: LiveData<Boolean> = _isShowDialogLoading

  private val _toastMessage = MutableLiveData<String>()
  val toastMessage: LiveData<String> = _toastMessage

  private val _modalMessage = MutableLiveData<String>()
  val modalMessage: LiveData<String> = _modalMessage

  private val _configData = MutableLiveData<String>()
  val configData: LiveData<String> = _configData

  private val _doEnterVerificationCode = MutableLiveData(false)
  val doEnterVerificationCode: LiveData<Boolean> = _doEnterVerificationCode

  private val _doNavigateToHome = MutableLiveData(false)
  val doNavigateToHome: LiveData<Boolean> = _doNavigateToHome

  private val compositeDisposable = CompositeDisposable()

  fun start() {
    _isShowDialogLoading.postValue(true)

    compositeDisposable.add(
      getStreamConfigsUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          if (it.isNotEmpty()) {
            _configData.postValue(it)
          }
        }
    )

    compositeDisposable.add(
      getStreamLoginStateUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .debounce(500L, TimeUnit.MILLISECONDS)
        .subscribe { loginState ->
          _isShowDialogLoading.postValue(false)

          when (loginState) {
            LoginState.Login -> {
              _doNavigateToHome.postValue(true)
            }
            is LoginState.Error -> {
              _modalMessage.postValue(loginState.message)
            }
            LoginState.VerificationCodeSent -> {
              _doEnterVerificationCode.postValue(true)
            }
            else -> {
              // do nothing
            }
          }
        }
    )

    viewModelScope.launch {
      when (val result = fetchConfigsUseCase()) {
        VoidResult.Success -> {
          onSuccessFetchConfigs()
        }
        is VoidResult.Error -> {
          onErrorFetchConfigs(result.message)
        }
      }
    }
  }

  private fun onSuccessFetchConfigs() {
    _isShowDialogLoading.postValue(false)
  }

  private fun onErrorFetchConfigs(msg: String) {
    _isShowDialogLoading.postValue(false)
    _toastMessage.value = msg
    _toastMessage.postValue("")
  }

  fun signInWithGoogle() {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = signInWithGoogleUseCase()) {
        VoidResult.Success -> {
          onSuccessSignInWithGoogle()
        }
        is VoidResult.Error -> {
          onErrorSignInWithGoogle(result.message)
        }
      }
    }
  }

  private fun onSuccessSignInWithGoogle() {
    _isShowDialogLoading.postValue(false)
  }

  private fun onErrorSignInWithGoogle(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.value = msg
    _modalMessage.postValue("")
  }

  fun startLoginWithPhoneNumber(phoneNumber: String) {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = startPhoneNumberVerificationUseCase(phoneNumber)) {
        VoidResult.Success -> {
          onSuccessStartPhoneNumberVerification()
        }
        is VoidResult.Error -> {
          onErrorStartPhoneNumberVerification(result.message)
        }
      }
    }
  }

  private fun onSuccessStartPhoneNumberVerification() {
    //_isShowDialogLoading.postValue(false)
  }

  private fun onErrorStartPhoneNumberVerification(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.value = msg
    _modalMessage.postValue("")
  }

  fun verifyPhoneNumber(code: String) {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = verifyPhoneNumberWithCodeUseCase(code)) {
        VoidResult.Success -> {
          onSuccessVerifyPhoneNumberWithCode()
        }
        is VoidResult.Error -> {
          onErrorVerifyPhoneNumberWithCode(result.message)
        }
      }
    }
  }

  private fun onSuccessVerifyPhoneNumberWithCode() {
    //_isShowDialogLoading.postValue(false)
  }

  private fun onErrorVerifyPhoneNumberWithCode(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.value = msg
    _modalMessage.postValue("")
  }

  fun resendVerificationCode() {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = resendVerificationCodeUseCase()) {
        VoidResult.Success -> {
          onSuccessResendVerificationCode()
        }
        is VoidResult.Error -> {
          onErrorResendVerificationCode(result.message)
        }
      }
    }
  }

  private fun onSuccessResendVerificationCode() {
    //_isShowDialogLoading.postValue(false)
  }

  private fun onErrorResendVerificationCode(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.value = msg
    _modalMessage.postValue("")
  }
}
