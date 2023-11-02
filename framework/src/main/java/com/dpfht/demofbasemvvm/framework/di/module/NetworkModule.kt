package com.dpfht.demofbasemvvm.framework.di.module

import com.dpfht.demofbasemvvm.data.datasource.RestDataSource
import com.dpfht.demofbasemvvm.framework.BuildConfig
import com.dpfht.demofbasemvvm.framework.Constants
import com.dpfht.demofbasemvvm.framework.data.core.rest.AuthInterceptor
import com.dpfht.demofbasemvvm.framework.data.core.rest.RestService
import com.dpfht.demofbasemvvm.framework.data.datasource.RestDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

  @Provides
  @Singleton
  fun httpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpLoggingInterceptor
  }

  @Provides
  @Singleton
  fun provideClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    val httpClientBuilder = OkHttpClient()
      .newBuilder()
    //.certificatePinner(certificatePinner)

    if (BuildConfig.DEBUG) {
      //return UnsafeOkHttpClient.getUnsafeOkHttpClient()
      httpClientBuilder.addInterceptor(httpLoggingInterceptor)
    }

    httpClientBuilder.addInterceptor(AuthInterceptor())

    return httpClientBuilder.build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl(Constants.RestApi.BASE_URL)
      .client(okHttpClient)
      .build()
  }

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): RestService {
    return retrofit.create(RestService::class.java)
  }

  @Provides
  @Singleton
  fun provideRestDataSource(restService: RestService): RestDataSource {
    return RestDataSourceImpl(restService)
  }
}
