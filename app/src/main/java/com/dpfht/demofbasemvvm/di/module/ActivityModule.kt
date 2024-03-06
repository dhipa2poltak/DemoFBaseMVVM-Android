package com.dpfht.demofbasemvvm.di.module

import android.content.Context
import com.dpfht.demofbasemvvm.framework.wrapper.FirebaseClientIdWrapper
import com.dpfht.demofbasemvvm.wrapper.FirebaseClientIdWrapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

  @Provides
  fun provideFirebaseClientIdWrapper(@ActivityContext context: Context): FirebaseClientIdWrapper {
    return FirebaseClientIdWrapperImpl(context)
  }
}
