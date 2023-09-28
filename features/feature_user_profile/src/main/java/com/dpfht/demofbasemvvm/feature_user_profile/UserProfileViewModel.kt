package com.dpfht.demofbasemvvm.feature_user_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.LoginState
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.UserProfileEntity
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamLoginStateUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetUserProfileUseCase
import com.dpfht.demofbasemvvm.domain.usecase.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
  private val getUserProfileUseCase: GetUserProfileUseCase,
  private val getStreamLoginStateUseCase: GetStreamLoginStateUseCase,
  private val logoutUseCase: LogOutUseCase
): ViewModel() {

  private val _isShowDialogLoading = MutableLiveData<Boolean>()
  val isShowDialogLoading: LiveData<Boolean> = _isShowDialogLoading

  private val _toastMessage = MutableLiveData<String>()
  val toastMessage: LiveData<String> = _toastMessage

  private val _modalMessage = MutableLiveData<String>()
  val modalMessage: LiveData<String> = _modalMessage

  private val _userProfileData = MutableLiveData<UserProfileEntity>()
  val userProfileData: LiveData<UserProfileEntity> = _userProfileData

  private val _doNavigateToLoginData = MutableLiveData(false)
  val doNavigateToLoginData: LiveData<Boolean> = _doNavigateToLoginData

  private val compositeDisposable = CompositeDisposable()

  fun start() {
    _isShowDialogLoading.postValue(true)

    compositeDisposable.add(
      getStreamLoginStateUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { loginState ->
          when (loginState) {
            LoginState.Logout -> {
              _doNavigateToLoginData.postValue(true)
            }
            is LoginState.Error -> {
              _modalMessage.postValue(loginState.message)
            }
            else -> {
              // do nothing
            }
          }
        }
    )

    viewModelScope.launch {
      when (val result = getUserProfileUseCase()) {
        is Result.Success -> {
          onSuccessGetUserProfile(result.value)
        }
        is Result.ErrorResult -> {
          onErrorGetUserProfile(result.message)
        }
      }
    }
  }

  private fun onSuccessGetUserProfile(userProfile: UserProfileEntity) {
    _isShowDialogLoading.postValue(false)
    _userProfileData.postValue(userProfile)
  }

  private fun onErrorGetUserProfile(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.postValue(msg)
  }

  fun logout() {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = logoutUseCase()) {
        VoidResult.Success -> {
          onSuccessLogout()
        }
        is VoidResult.Error -> {
          onErrorLogout(result.message)
        }
      }
    }
  }

  private fun onSuccessLogout() {
    _isShowDialogLoading.postValue(false)
  }

  private fun onErrorLogout(msg: String) {
    _isShowDialogLoading.postValue(false)
    _toastMessage.postValue(msg)
  }
}
