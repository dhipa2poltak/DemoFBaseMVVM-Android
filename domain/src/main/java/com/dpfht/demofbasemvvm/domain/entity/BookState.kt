package com.dpfht.demofbasemvvm.domain.entity

sealed class BookState {
  object None: BookState()
  data class BookAdded(val message: String): BookState()
  data class BookUpdated(val message: String): BookState()
  data class BookDeleted(val message: String): BookState()
  data class BooksData(val books: List<BookEntity>): BookState()
  data class BookData(val book: BookEntity): BookState()
  data class ErrorAddBook(val message: String): BookState()
  data class ErrorUpdateBook(val message: String): BookState()
  data class ErrorDeleteBook(val message: String): BookState()
  data class ErrorGetAllBooks(val message: String): BookState()
  data class ErrorGetBook(val message: String): BookState()
}
