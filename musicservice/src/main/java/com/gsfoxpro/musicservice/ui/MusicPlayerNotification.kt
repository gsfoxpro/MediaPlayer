package com.gsfoxpro.musicservice.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.gsfoxpro.musicservice.model.AudioTrack

class MusicPlayerNotification {
    companion object {
        val NOTIFICATION_ID = 1001

        fun show(context: Context, intentActionPlay: String, audioTrack: AudioTrack, played: Boolean) {
            val playIntent = Intent(intentActionPlay)
            val playPendingIntent = PendingIntent.getService(context, 0, playIntent, 0)

            val smallIcon = if (played) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause
            val largeIcon = if (played) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

            val notification = NotificationCompat.Builder(context, NOTIFICATION_ID.toString())
                    .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                            .setShowCancelButton(true)


                    )
                    .setSmallIcon(smallIcon)
                    .setTicker("")
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true)
                    .build()

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }

        fun hide(context: Context) {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }
    }
}