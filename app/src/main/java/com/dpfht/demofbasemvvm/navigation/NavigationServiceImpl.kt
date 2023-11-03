package com.dpfht.demofbasemvvm.navigation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dpfht.demofbasemvvm.BuildConfig
import com.dpfht.demofbasemvvm.MainActivity
import com.dpfht.demofbasemvvm.R
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.framework.navigation.NavigationService

class NavigationServiceImpl(
  private val navController: NavController,
  private val activity: AppCompatActivity
): NavigationService {

  override fun navigateToLogin() {
    val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
    navGraph.setStartDestination(R.id.loginFragment)

    navController.graph = navGraph

    val appBarConfiguration = AppBarConfiguration(
      setOf(R.id.loginFragment)
    )

    activity.setupActionBarWithNavController(navController, appBarConfiguration)
    (activity as MainActivity).binding.bottomNav.visibility = View.GONE
  }

  override fun navigateToHome() {
    val navGraph = navController.navInflater.inflate(R.navigation.home_graph)
    navController.graph = navGraph

    //Give the action bar the correct title for the current screen (this uses the fragment label)
    val appBarConfiguration = AppBarConfiguration(
      setOf(R.id.bookListFragment, R.id.pushMessageFragment,  R.id.userProfileFragment)
    )

    activity.setupActionBarWithNavController(navController, appBarConfiguration)

    (activity as MainActivity).binding.bottomNav.setupWithNavController(navController)
    activity.binding.bottomNav.visibility = View.VISIBLE
  }

  override fun navigateInHomeToAddBook() {
    val builder = Uri.Builder()
    builder.scheme("android-app")
      .authority(BuildConfig.APPLICATION_ID)
      .appendPath("add_edit_book_fragment")

    navController.navigate(
      NavDeepLinkRequest.Builder
      .fromUri(builder.build())
      .build())
  }

  override fun navigateInHomeFromDetailsToEditBook(book: BookEntity) {
    val bundle = Bundle()
    bundle.putSerializable("book_arg", book)

    navController.navigate(R.id.action_from_details_to_edit, bundle)
  }

  override fun navigateInHomeToBookDetails(bookId: String) {
    val builder = Uri.Builder()
    builder.scheme("android-app")
      .authority(BuildConfig.APPLICATION_ID)
      .appendPath("book_details_fragment")
      .appendQueryParameter("book_id", bookId)

    navController.navigate(
      NavDeepLinkRequest.Builder
        .fromUri(builder.build())
        .build())
  }

  override fun navigateUp() {
    navController.navigateUp()
  }
}
