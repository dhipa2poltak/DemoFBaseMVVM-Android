package com.dpfht.demofbasemvvm.feature_push_message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.Result
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.usecase.FetchFCMQuotaUseCase
import com.dpfht.demofbasemvvm.domain.usecase.FetchFCMTokenUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamFCMQuotaUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamFCMTokenUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamPushMessageUseCase
import com.dpfht.demofbasemvvm.domain.usecase.PostFCMMessageUseCase
import com.dpfht.demofbasemvvm.domain.usecase.SetFCMQuotaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushMessageViewModel @Inject constructor(
  private val getStreamPushMessageUseCase: GetStreamPushMessageUseCase,
  private val getStreamFCmTokenUseCase: GetStreamFCMTokenUseCase,
  private val fetchFCMTokenUseCase: FetchFCMTokenUseCase,
  private val postFCMMessageUseCase: PostFCMMessageUseCase,
  private val fetchFCMQuotaUseCase: FetchFCMQuotaUseCase,
  private val setFCMQuotaUseCase: SetFCMQuotaUseCase,
  private val getStreamFCMQuotaUseCase: GetStreamFCMQuotaUseCase
): ViewModel() {

  private val _isShowDialogLoading = MutableLiveData<Boolean>()
  val isShowDialogLoading: LiveData<Boolean> = _isShowDialogLoading

  private val _toastMessage = MutableLiveData<String>()
  val toastMessage: LiveData<String> = _toastMessage

  private val _modalMessage = MutableLiveData<String>()
  val modalMessage: LiveData<String> = _modalMessage

  private val _pushMessageData = MutableLiveData<Pair<String, String>>()
  val pushMessageData: LiveData<Pair<String, String>> = _pushMessageData

  private val _fcmTokenData = MutableLiveData<String>()
  val fcmTokenData: LiveData<String> = _fcmTokenData

  private val _fcmQuotaData = MutableLiveData<Int>()
  val fcmQuotaData: LiveData<Int> = _fcmQuotaData

  private var quotaCount = 0

  private val compositeDisposable = CompositeDisposable()

  fun start() {
    _isShowDialogLoading.postValue(true)

    compositeDisposable.add(
      getStreamFCmTokenUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          _fcmTokenData.postValue(it)
        }
    )

    compositeDisposable.add(
      getStreamPushMessageUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val pair = Pair(it.title, it.message)
          _pushMessageData.postValue(pair)
        }
    )

    compositeDisposable.add(
      getStreamFCMQuotaUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { result ->
          when (result) {
            is Result.Success -> {
              quotaCount = result.value
              if (quotaCount != -1) {
                _isShowDialogLoading.postValue(false)
                _fcmQuotaData.postValue(quotaCount)
              }
            }
            is Result.ErrorResult -> {
              _isShowDialogLoading.postValue(false)
              _modalMessage.postValue(result.message)
            }
          }
        }
    )

    viewModelScope.launch {
      when (val result = fetchFCMTokenUseCase()) {
        VoidResult.Success -> {
          onSuccessFetchFCMToken()
        }
        is VoidResult.Error -> {
          onErrorFetchFCMToken(result.message)
        }
      }

      when (val result = fetchFCMQuotaUseCase()) {
        VoidResult.Success -> {
          onSuccessFetchFCMQuota()
        }
        is VoidResult.Error -> {
          onErrorFetchFCMQuota(result.message)
        }
      }
    }
  }

  private fun onSuccessFetchFCMToken() {
    //_isShowDialogLoading.postValue(false)
  }

  private fun onErrorFetchFCMToken(msg: String) {
    //_isShowDialogLoading.postValue(false)
    _modalMessage.postValue(msg)
  }

  private fun onSuccessFetchFCMQuota() {
    //_isShowDialogLoading.postValue(false)
  }

  private fun onErrorFetchFCMQuota(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.postValue(msg)
  }

  fun postFCMMessage(to: String, title: String, message: String) {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = postFCMMessageUseCase(to, title, message)) {
        is Result.Success -> {
          val response = result.value
          if (response.failure == 1) {
            onErrorPostFCMMessage(response.results[0].error)
          } else {
            onSuccessPostFCMMessage()
          }
        }
        is Result.ErrorResult -> {
          onErrorPostFCMMessage(result.message)
        }
      }
    }
  }

  private fun onSuccessPostFCMMessage() {
    //_isShowDialogLoading.postValue(false)

    viewModelScope.launch {
      when (val result = setFCMQuotaUseCase(quotaCount - 1)) {
        VoidResult.Success -> {
          onSuccessSetFCMQuota()
        }
        is VoidResult.Error -> {
          onErrorSetFCMQuota(result.message)
        }
      }
    }
  }

  private fun onErrorPostFCMMessage(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.postValue(msg)
  }

  private fun onSuccessSetFCMQuota() {
    _isShowDialogLoading.postValue(false)
  }

  private fun onErrorSetFCMQuota(msg: String) {
    _isShowDialogLoading.postValue(false)
    _modalMessage.postValue(msg)
  }
}
