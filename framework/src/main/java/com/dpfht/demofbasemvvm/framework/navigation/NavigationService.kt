package com.dpfht.demofbasemvvm.framework.navigation

import com.dpfht.demofbasemvvm.domain.entity.BookEntity

interface NavigationService {

  fun navigateToLogin()
  fun navigateToHome()
  fun navigateInHomeToAddBook()
  fun navigateInHomeFromDetailsToEditBook(book: BookEntity)
  fun navigateInHomeToBookDetails(bookId: String)
}
