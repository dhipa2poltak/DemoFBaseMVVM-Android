package com.dpfht.demofbasemvvm.di.module

import android.content.Context
import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.data.repository.AppRepositoryImpl
import com.dpfht.demofbasemvvm.datasource.FirebaseDataSourceImpl
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

  @Provides
  @Singleton
  fun provideFirebaseDataSource(@ApplicationContext context: Context): FirebaseDataSource {
    return FirebaseDataSourceImpl(context)
  }

  @Provides
  @Singleton
  fun provideAppRepository(firebaseDataSource: FirebaseDataSource, restDataSource: RestDataSource): AppRepository {
    return AppRepositoryImpl(firebaseDataSource, restDataSource)
  }
}
