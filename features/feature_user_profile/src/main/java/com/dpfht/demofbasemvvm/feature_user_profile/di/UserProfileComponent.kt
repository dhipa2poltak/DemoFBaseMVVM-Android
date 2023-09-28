package com.dpfht.demofbasemvvm.feature_user_profile.di

import android.content.Context
import com.dpfht.demofbasemvvm.feature_user_profile.UserProfileFragment
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [NavigationServiceDependency::class])
interface UserProfileComponent {

  fun inject(userProfileFragment: UserProfileFragment)

  @Component.Builder
  interface Builder {
    fun context(@BindsInstance context: Context): Builder
    fun navDependency(navigationServiceDependency: NavigationServiceDependency): Builder
    fun build(): UserProfileComponent
  }
}
