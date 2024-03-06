package com.dpfht.demofbasemvvm.firebase.di.module

import android.content.Context
import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.firebase.FirebaseDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

  @Provides
  @Singleton
  fun provideFirebaseDataSource(@ApplicationContext context: Context): FirebaseDataSource {
    return FirebaseDataSourceImpl(context)
  }
}
