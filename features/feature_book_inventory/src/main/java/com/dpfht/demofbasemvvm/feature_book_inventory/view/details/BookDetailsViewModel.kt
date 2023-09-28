package com.dpfht.demofbasemvvm.feature_book_inventory.view.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.usecase.DeleteBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamBookStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
  private val getStreamBookStateUseCase: GetStreamBookStateUseCase,
  private val getBookUseCase: GetBookUseCase,
  private val deleteBookUseCase: DeleteBookUseCase
): ViewModel() {

  private val _isShowDialogLoading = MutableLiveData<Boolean>()
  val isShowDialogLoading: LiveData<Boolean> = _isShowDialogLoading

  private val _toastMessage = MutableLiveData<String>()
  val toastMessage: LiveData<String> = _toastMessage

  private val _modalMessage = MutableLiveData<String>()
  val modalMessage: LiveData<String> = _modalMessage

  private val _bookData = MutableLiveData<BookEntity>()
  val bookData: LiveData<BookEntity> = _bookData

  private val _closeScreenData = MutableLiveData<Boolean>()
  val closeScreenData: LiveData<Boolean> = _closeScreenData

  private val compositeDisposable = CompositeDisposable()

  var bookId = ""
  var theBook: BookEntity? = null

  fun start() {
    if (bookId.isNotEmpty()) {
      compositeDisposable.add(
        getStreamBookStateUseCase()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { bookState ->
            when (bookState) {
              is BookState.BookData -> {
                _isShowDialogLoading.postValue(false)
                theBook = bookState.book
                theBook?.let {
                  _bookData.postValue(it)
                }
              }
              is BookState.BookDeleted -> {
                _isShowDialogLoading.postValue(false)

                _toastMessage.value = bookState.message
                _toastMessage.value = ""

                _closeScreenData.postValue(true)
              }
              is BookState.ErrorGetBook -> {
                _isShowDialogLoading.postValue(false)

                _modalMessage.value = bookState.message
                _modalMessage.value = ""
              }
              is BookState.ErrorDeleteBook -> {
                _isShowDialogLoading.postValue(false)

                _modalMessage.value = bookState.message
                _modalMessage.value = ""
              }
              else -> {
                //-- ignore
              }
            }
          }
      )

      _isShowDialogLoading.postValue(true)

      viewModelScope.launch {
        when (val result = getBookUseCase(bookId)) {
          VoidResult.Success -> {
            onSuccessGetBook()
          }
          is VoidResult.Error -> {
            onErrorGetBook(result.message)
          }
        }
      }
    }
  }

  private fun onSuccessGetBook() {}

  private fun onErrorGetBook(msg: String) {
    _isShowDialogLoading.postValue(false)

    _modalMessage.value = msg
    _modalMessage.value = ""
  }

  fun deleteBook() {
    if (theBook != null) {
      _isShowDialogLoading.postValue(true)

      viewModelScope.launch {
        theBook?.let {
          when (val result = deleteBookUseCase(it)) {
            VoidResult.Success -> {
              onSuccessDeleteBook()
            }
            is VoidResult.Error -> {
              onErrorDeleteBook(result.message)
            }
          }
        }
      }
    }
  }

  private fun onSuccessDeleteBook() {}

  private fun onErrorDeleteBook(msg: String) {
    _isShowDialogLoading.postValue(false)

    _modalMessage.value = msg
    _modalMessage.value = ""
  }
}
