package com.dpfht.demofbasemvvm.framework.di.module

import com.dpfht.demofbasemvvm.data.datasource.FirebaseDataSource
import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.data.repository.AppRepositoryImpl
import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

  @Provides
  @Singleton
  fun provideAppRepository(firebaseDataSource: FirebaseDataSource, restDataSource: RestDataSource): AppRepository {
    return AppRepositoryImpl(firebaseDataSource, restDataSource)
  }
}
