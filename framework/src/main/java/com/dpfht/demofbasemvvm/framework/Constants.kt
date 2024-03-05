package com.dpfht.demofbasemvvm.framework

object Constants {

  const val DELAY_SPLASH = 3000
  const val BOOK_QUOTA = 5

  object RestApi {
    const val BASE_URL = "https://fcm.googleapis.com/"

    const val PATH_FCM_SEND = "fcm/send"

    var HEADER_AUTHORIZATION_VALUE = "key=${Configs.FCMConfig.FCM_SERVER_KEY}"
  }

  object BroadcastTagName {
    const val RECEIVED_PUSH_MESSAGE = "received_push_message"
    const val RECEIVED_NEW_FCM_TOKEN = "received_new_fcm_token"
  }

  object ExtraParamName {
    const val EXTRA_TITLE = "title"
    const val EXTRA_MESSAGE = "message"
    const val EXTRA_FCM_TOKEN = "fcm_token"
  }

  object RemoteConfigs {
    const val KEY_CONFIG_TITLE_LOGIN_SCREEN = "title_login_screen"
  }

  object FCM {
    object MessageKeys {
      const val KEY_TITLE = "title"
      const val KEY_BODY = "body"
    }

    const val MAX_QUOTA_PER_DAY = 3
  }

  object Analytics {
    object Events {
      const val EVENT_SPLASH_TO_LOGIN = "splash_entering_to_login"
      const val EVENT_SPLASH_TO_HOME = "splash_entering_to_home"

      object Params {
        const val KEY_PLATFORM = "platform"

        const val VALUE_PLATFORM = "Android"
      }
    }
  }
}
