package com.dpfht.demofbasemvvm.feature_login_register.di

import android.content.Context
import com.dpfht.demofbasemvvm.feature_login_register.LoginFragment
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [NavigationServiceDependency::class])
interface LoginComponent {

  fun inject(loginFragment: LoginFragment)

  @Component.Builder
  interface Builder {
    fun context(@BindsInstance context: Context): Builder
    fun navDependency(navigationServiceDependency: NavigationServiceDependency): Builder
    fun build(): LoginComponent
  }
}
