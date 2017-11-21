package com.gsfoxpro.musicservice.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.ui.MusicPlayerNotification


class MusicService : Service() {

    inner class LocalBinder(val musicService: MusicService = this@MusicService) : Binder()
    private val binder = LocalBinder()

    var mediaSession: MediaSessionCompat? = null
        private set

    var musicRepo: MusicRepo? = null

    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val stateBuilder:PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
         .setActions(
                 PlaybackStateCompat.ACTION_PLAY
                 or PlaybackStateCompat.ACTION_STOP
                 or PlaybackStateCompat.ACTION_PAUSE
                 or PlaybackStateCompat.ACTION_PLAY_PAUSE
                 or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                 or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

    private var lastInitializedTrack: AudioTrack? = null

    private var mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            super.onAddQueueItem(description)
        }

        override fun onPlay() {
            play()
        }

        override fun onPause() {
            pause()
        }

        override fun onStop() {
            stop()
        }

        override fun onSkipToNext() {
            next()
        }

        override fun onSkipToPrevious() {
            prev()
        }

    }

    private lateinit var exoPlayer: SimpleExoPlayer

    override fun onBind(intent: Intent?) = binder

    override fun onCreate() {
        super.onCreate()
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java)

        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(mediaSessionCallback)
            setMediaButtonReceiver(PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0))
        }

        exoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
        exoPlayer.addListener(object : Player.EventListener {
            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (musicRepo?.autoPlay == true && playWhenReady && playbackState == Player.STATE_ENDED) {
                    mediaSessionCallback.onSkipToNext()
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity() {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        mediaSession?.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initTrack(audioTrack: AudioTrack?) {
        audioTrack?.let {
            metadataBuilder
                    .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, it.imageUrl)
                    //.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), track.getBitmapResId()))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, it.subtitle)
                    //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it.title)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, exoPlayer.duration) //тут по идее уже надо бы проинициализировать трек
            mediaSession?.setMetadata(metadataBuilder.build())

            val mediaSource = ExtractorMediaSource(
                    Uri.parse(it.url),
                    DefaultDataSourceFactory(applicationContext, "user-agent"),
                    DefaultExtractorsFactory(),
                    null,
                    null)
            exoPlayer.prepare(mediaSource)

            lastInitializedTrack = it
        }
    }

    private fun play() {
        val currentTrack = musicRepo?.currentAudioTrack
        play(currentTrack)
    }

    private fun play(audioTrack: AudioTrack?) {
        if (lastInitializedTrack?.url != audioTrack?.url) {
            initTrack(audioTrack)
        }

        mediaSession?.apply {
            isActive = true
            setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
            MusicPlayerNotification.show(this@MusicService, this)
        }

        exoPlayer.playWhenReady = true
    }

    private fun pause() {
        exoPlayer.playWhenReady = false

        mediaSession?.apply {
            setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
            MusicPlayerNotification.show(this@MusicService, this)
        }
    }

    private fun stop() {
        exoPlayer.stop()
        lastInitializedTrack = null

        mediaSession?.apply {
            isActive = false
            setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
        }
        MusicPlayerNotification.hide(this@MusicService)
    }

    private fun next() {
        play(musicRepo?.nextAudioTrack)
    }

    private fun prev() {
        play(musicRepo?.prevAudioTrack)
    }
}