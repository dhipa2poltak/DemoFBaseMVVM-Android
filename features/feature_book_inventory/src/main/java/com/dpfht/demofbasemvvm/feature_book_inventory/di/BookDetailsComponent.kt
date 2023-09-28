package com.dpfht.demofbasemvvm.feature_book_inventory.di

import android.content.Context
import com.dpfht.demofbasemvvm.feature_book_inventory.view.details.BookDetailsFragment
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [NavigationServiceDependency::class])
interface BookDetailsComponent {

  fun inject(bookDetailsFragment: BookDetailsFragment)

  @Component.Builder
  interface Builder {
    fun context(@BindsInstance context: Context): Builder
    fun navDependency(navigationServiceDependency: NavigationServiceDependency): Builder
    fun build(): BookDetailsComponent
  }
}
