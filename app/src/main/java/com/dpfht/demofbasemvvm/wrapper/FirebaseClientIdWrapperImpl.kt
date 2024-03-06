package com.dpfht.demofbasemvvm.wrapper

import android.content.Context
import com.dpfht.demofbasemvvm.R
import com.dpfht.demofbasemvvm.framework.wrapper.FirebaseClientIdWrapper

class FirebaseClientIdWrapperImpl(
  private val context: Context
): FirebaseClientIdWrapper {
  override fun getClientId(): String {
    return context.getString(R.string.default_web_client_id)
  }
}
