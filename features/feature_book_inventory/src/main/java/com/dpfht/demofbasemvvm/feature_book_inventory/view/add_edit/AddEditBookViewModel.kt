package com.dpfht.demofbasemvvm.feature_book_inventory.view.add_edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.usecase.AddBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamBookStateUseCase
import com.dpfht.demofbasemvvm.domain.usecase.UpdateBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditBookViewModel @Inject constructor(
  private val getStreamBookStateUseCase: GetStreamBookStateUseCase,
  private val addBookUseCase: AddBookUseCase,
  private val updateBookUseCase: UpdateBookUseCase
): ViewModel() {

  private val _isShowDialogLoading = MutableLiveData<Boolean>()
  val isShowDialogLoading: LiveData<Boolean> = _isShowDialogLoading

  private val _toastMessage = MutableLiveData<String>()
  val toastMessage: LiveData<String> = _toastMessage

  private val _modalMessage = MutableLiveData<String>()
  val modalMessage: LiveData<String> = _modalMessage

  private val _resetFormBook = MutableLiveData<Boolean>()
  val resetFormBook: LiveData<Boolean> = _resetFormBook

  private val _closeScreenData = MutableLiveData<Boolean>()
  val closeScreenData: LiveData<Boolean> = _closeScreenData

  private val compositeDisposable = CompositeDisposable()

  var theBook: BookEntity? = null
  var uriLocalImage: Uri? = null

  fun start() {
    compositeDisposable.add(
      getStreamBookStateUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { bookState ->
          when (bookState) {
            is BookState.BookAdded -> {
              _isShowDialogLoading.postValue(false)

              _toastMessage.value = bookState.message
              _toastMessage.value = ""

              _resetFormBook.value = true
              _resetFormBook.value = false
            }
            is BookState.BookUpdated -> {
              _isShowDialogLoading.postValue(false)

              _toastMessage.value = bookState.message
              _toastMessage.value = ""

              _closeScreenData.postValue(true)
            }
            is BookState.ErrorAddBook -> {
              _isShowDialogLoading.postValue(false)

              _modalMessage.value = bookState.message
              _modalMessage.value = ""
            }
            is BookState.ErrorUpdateBook -> {
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
  }

  fun addBook(title: String, writer: String, description: String, stock: Int) {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      val book = BookEntity(title = title, writer = writer, description = description, stock = stock)
      val uriStringImage = uriLocalImage?.toString() ?: ""
      when (val result = addBookUseCase(book, uriStringImage)) {
        VoidResult.Success -> {
          onSuccessAddBook()
        }
        is VoidResult.Error -> {
          onErrorAddBook(result.message)
        }
      }
    }
  }

  private fun onSuccessAddBook() {}

  private fun onErrorAddBook(msg: String) {
    viewModelScope.launch(Dispatchers.Main) {
      _isShowDialogLoading.postValue(false)

      _modalMessage.value = msg
      _modalMessage.value = ""
    }
  }

  fun updateBook(title: String, writer: String, description: String, stock: Int) {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      theBook?.let {
        val book = it.copy(title = title, writer = writer, description = description, stock = stock)

        when (val result = updateBookUseCase(book, uriLocalImage?.toString() ?: "")) {
          VoidResult.Success -> {
            onSuccessUpdateBook()
          }

          is VoidResult.Error -> {
            onErrorUpdateBook(result.message)
          }
        }
      }
    }
  }

  private fun onSuccessUpdateBook() {}

  private fun onErrorUpdateBook(msg: String) {
    viewModelScope.launch(Dispatchers.Main) {
      _isShowDialogLoading.postValue(false)

      _modalMessage.value = msg
      _modalMessage.value = ""
    }
  }
}
