package com.dpfht.demofbasemvvm.datasource.remote.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat.Builder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dpfht.demofbasemvvm.MainActivity
import com.dpfht.demofbasemvvm.R
import com.dpfht.demofbasemvvm.framework.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireMsgService : FirebaseMessagingService() {
  private val notificationChannelId = "com.dpfht.demofbasemvvm"
  private val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
  private val channelName = "My Background Service"

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    val title = remoteMessage.data[Constants.FCM.MessageKeys.KEY_TITLE]
    val body = remoteMessage.data[Constants.FCM.MessageKeys.KEY_BODY]

    val itn = Intent(Constants.BroadcastTagName.RECEIVED_PUSH_MESSAGE)
    itn.putExtra(Constants.ExtraParamName.EXTRA_TITLE, title ?: "")
    itn.putExtra(Constants.ExtraParamName.EXTRA_MESSAGE, body ?: "")
    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(itn)

    showNotification(title, body)
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    //Log.d("Not", "Token [$token]")

    val itn = Intent(Constants.BroadcastTagName.RECEIVED_NEW_FCM_TOKEN)
    itn.putExtra(Constants.ExtraParamName.EXTRA_FCM_TOKEN, token)
    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(itn)
  }

  private fun showNotification(title: String?, body: String?) {
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val notificationIntent = Intent(this, MainActivity::class.java)
    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //or Intent.FLAG_ACTIVITY_CLEAR_TASK
    val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

    val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_message)
    val notification = Builder(this, notificationChannelId)
      .setContentTitle(title)
      .setTicker("this is ticker")
      .setContentText(body)
      .setSmallIcon(R.drawable.ic_message)
      //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val chan = NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_HIGH)
      chan.enableLights(true)
      chan.lightColor = Color.BLUE
      chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
      chan.setSound(defaultSoundUri, null)
      chan.description = body

      notificationManager.createNotificationChannel(chan)
    }

    notificationManager.notify(1410, notification.build())
  }
}
