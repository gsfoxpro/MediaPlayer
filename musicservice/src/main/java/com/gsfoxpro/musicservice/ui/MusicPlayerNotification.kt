package com.gsfoxpro.musicservice.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class MusicPlayerNotification {
    companion object {
        val NOTIFICATION_ID = 1001
        val IMAGE_CASH_SIZE = 5

        private var notificationBuilder: NotificationCompat.Builder? = null
        private val imageCache: MutableMap<Uri, Bitmap> = HashMap()

        fun show(context: Context, mediaSession: MediaSessionCompat) {
            val controller = mediaSession.controller
            val mediaMetadata = controller.metadata
            val description = mediaMetadata.description

            val playing = controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING

            val smallIcon = if (playing) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause
            val largeImage = imageCache[description.iconUri]

            val stopIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

            notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_ID.toString())
                    .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(1)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(stopIntent)
                            .setMediaSession(mediaSession.sessionToken)
                    )
                    .addAction(android.R.drawable.ic_media_previous, "", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                    .addAction(if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play, "", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE))
                    .addAction(android.R.drawable.ic_media_next, "", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                    .setContentIntent(controller.sessionActivity)
                    .setDeleteIntent(stopIntent)
                    .setContentTitle(description.title)
                    .setContentText(description.subtitle)
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(largeImage ?: BitmapFactory.decodeResource(context.resources, android.R.mipmap.sym_def_app_icon)) //временно
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder?.build())

            if (largeImage == null) {
                updateImageAsync(context, description.iconUri)
            }

        }

        fun hide(context: Context) {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }

        private fun updateImageAsync(context: Context, imageUri: Uri?) {
            if (imageUri == null) {
                return
            }
            Glide.with(context)
                    .asBitmap()
                    .load(imageUri)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                            resource?.let { bitmap ->
                                if (imageCache.size > IMAGE_CASH_SIZE) {
                                    imageCache.clear()
                                }
                                imageCache.put(imageUri, bitmap)
                                notificationBuilder?.setLargeIcon(bitmap)
                                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder?.build())
                            }
                        }
                    })
        }

    }
}