package com.example.musicapimvvm.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val ACTION_NEXT = "NEXT"
        const val ACTION_PLAY = "PLAY"
        const val ACTION_PREVIOUS = "PREVIOUS"
        const val ACTION_CLEAR = "CLEAR"
    }

    override fun onCreate() {
        super.onCreate()

        createChanelNotification()
    }

    private fun createChanelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "Channel ID", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Channel Description"
            notificationChannel.setSound(null, null)

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager?.let {
                notificationManager.createNotificationChannel(
                    notificationChannel
                )
            }
        }
    }

}