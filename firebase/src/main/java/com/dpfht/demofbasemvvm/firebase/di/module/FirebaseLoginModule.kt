package com.dpfht.demofbasemvvm.firebase.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dpfht.demofbasemvvm.data.datasource.FirebaseLoginDataSource
import com.dpfht.demofbasemvvm.firebase.FirebaseLoginDataSourceImpl
import com.dpfht.demofbasemvvm.framework.wrapper.FirebaseClientIdWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class FirebaseLoginModule {

  @Provides
  fun provideFirebaseLoginDataSource(
    @ActivityContext context: Context,
    firebaseClientIdWrapper: FirebaseClientIdWrapper
  ): FirebaseLoginDataSource {
    return FirebaseLoginDataSourceImpl(context as AppCompatActivity, firebaseClientIdWrapper)
  }
}
