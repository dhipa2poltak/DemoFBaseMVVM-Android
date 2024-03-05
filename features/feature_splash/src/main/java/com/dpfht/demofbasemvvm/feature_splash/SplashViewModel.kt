package com.dpfht.demofbasemvvm.feature_splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.Result.Error
import com.dpfht.demofbasemvvm.domain.usecase.IsLoginUseCase
import com.dpfht.demofbasemvvm.domain.usecase.LogEventUseCase
import com.dpfht.demofbasemvvm.framework.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
  private val isLoginUseCase: IsLoginUseCase,
  private val logEventUseCase: LogEventUseCase
): ViewModel() {

  private val _isLoginData = MutableLiveData<Boolean?>()
  val isLoginData: LiveData<Boolean?> = _isLoginData

  private val _errorMessage = MutableLiveData<String>()
  val errorMessage: LiveData<String> = _errorMessage

  fun start() {
    viewModelScope.launch {
      delay(Constants.DELAY_SPLASH.toLong())

      when (val result = isLoginUseCase()) {
        is Result.Success -> {
          onSuccess(result.value)
        }
        is Error -> {
          onError(result.message)
        }
      }
    }
  }

  private suspend fun onSuccess(isLogin: Boolean) {
    logEventUseCase(if (isLogin) {
      Constants.Analytics.Events.EVENT_SPLASH_TO_HOME
    } else {
      Constants.Analytics.Events.EVENT_SPLASH_TO_LOGIN
    }, mapOf(Constants.Analytics.Events.Params.KEY_PLATFORM to Constants.Analytics.Events.Params.VALUE_PLATFORM))

    _isLoginData.postValue(isLogin)
  }

  private fun onError(message: String) {
    _errorMessage.value = message
    _errorMessage.postValue("")
  }
}
