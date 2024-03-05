package com.dpfht.demofbasemvvm

import android.app.Application
import com.dpfht.demofbasemvvm.framework.Configs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TheApplication: Application() {

  override fun onCreate() {
    Configs.FCMConfig.FCM_SERVER_KEY = BuildConfig.SERVER_KEY
    super.onCreate()
    instance = this
  }

  companion object {
    lateinit var instance: TheApplication
  }
}
