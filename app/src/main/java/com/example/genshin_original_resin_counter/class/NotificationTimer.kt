package com.example.genshin_original_resin_counter.`class`

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.genshin_original_resin_counter.MainActivity
import com.example.genshin_original_resin_counter.R
import com.example.genshin_original_resin_counter.util.MessagesInterface

class NotificationTimer : Application() {

    companion object {
        const val COUNTER_CHANNEL_ID = "counter_channel"
    }

    fun showNotification(current: Context, message:String) {
        val notificationManager =
            current.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val activityIntent = Intent(current, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            current,
            1,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(current, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(MessagesInterface.NOTIFICATION_HEADER)
            .setContentText(message).setContentIntent(activityPendingIntent).build()

        notificationManager.notify(COUNTER_CHANNEL_ID, 1, notification)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                COUNTER_CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for Genshin Alert"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}