package com.dpfht.demofbasemvvm.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dpfht.demofbasemvvm.data.datasource.FirebaseLoginDataSource
import com.dpfht.demofbasemvvm.datasource.remote.FirebaseLoginDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class FirebaseLoginModule {

  @Provides
  fun provideFirebaseLoginDataSource(@ActivityContext context: Context): FirebaseLoginDataSource {
    return FirebaseLoginDataSourceImpl(context as AppCompatActivity)
  }
}
