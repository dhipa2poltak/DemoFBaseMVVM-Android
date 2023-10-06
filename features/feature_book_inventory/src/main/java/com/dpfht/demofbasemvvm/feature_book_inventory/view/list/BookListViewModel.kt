package com.dpfht.demofbasemvvm.feature_book_inventory.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.domain.entity.BookState
import com.dpfht.demofbasemvvm.domain.entity.VoidResult
import com.dpfht.demofbasemvvm.domain.usecase.GetAllBooksUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamBookStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
  private val getStreamBookStateUseCase: GetStreamBookStateUseCase,
  private val getAllBooksUseCase: GetAllBooksUseCase
): ViewModel() {

  private val _isShowDialogLoading = MutableLiveData<Boolean>()
  val isShowDialogLoading: LiveData<Boolean> = _isShowDialogLoading

  private val _toastMessage = MutableLiveData<String>()
  val toastMessage: LiveData<String> = _toastMessage

  private val _modalMessage = MutableLiveData<String>()
  val modalMessage: LiveData<String> = _modalMessage

  private val _refreshBooksData = MutableLiveData<Boolean>()
  val refreshBooksData: LiveData<Boolean> = _refreshBooksData

  val books = arrayListOf<BookEntity>()

  private val compositeDisposable = CompositeDisposable()

  fun start() {
    compositeDisposable.add(
      getStreamBookStateUseCase()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { bookState ->
          when (bookState) {
            is BookState.BooksData -> {
              books.clear()
              books.addAll(bookState.books)

              _isShowDialogLoading.postValue(false)
              _refreshBooksData.value = true
              _refreshBooksData.value = false
            }
            is BookState.ErrorGetAllBooks -> {
              _isShowDialogLoading.postValue(false)

              _modalMessage.value = bookState.message
              _modalMessage.value = ""
            }
            else -> {
              // ignore
            }
          }
        }
    )
  }

  fun getAllBooks() {
    _isShowDialogLoading.postValue(true)

    viewModelScope.launch {
      when (val result = getAllBooksUseCase()) {
        VoidResult.Success -> {
          onSuccessGetAllBooks()
        }
        is VoidResult.Error -> {
          onErrorGetAllBooks(result.message)
        }
      }
    }
  }

  private fun onSuccessGetAllBooks() {}

  private fun onErrorGetAllBooks(msg: String) {
    _isShowDialogLoading.postValue(false)

    _modalMessage.value = msg
    _modalMessage.postValue("")
  }
}
