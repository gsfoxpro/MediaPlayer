package com.gsfoxpro.musicservice.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.gsfoxpro.musicservice.IPlayer
import com.gsfoxpro.musicservice.model.AudioTrack
import android.app.PendingIntent
import android.content.Context
import android.media.AudioManager


class MusicService : Service(), IPlayer {

    inner class LocalBinder(val musicService: MusicService = this@MusicService) : Binder()
    private val binder = LocalBinder()

    private var mediaSession: MediaSessionCompat? = null

    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val stateBuilder:PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
         .setActions(
                 PlaybackStateCompat.ACTION_PLAY
                 or PlaybackStateCompat.ACTION_STOP
                 or PlaybackStateCompat.ACTION_PAUSE
                 or PlaybackStateCompat.ACTION_PLAY_PAUSE
                 or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                 or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)



    val mediaSessionToken: MediaSessionCompat.Token?
        get() = mediaSession?.sessionToken

    private var mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            if (_state == IPlayer.STATE_IDLE) {
                initCurrentTrack()
            }

            val metadata = metadataBuilder
                    //.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), track.getBitmapResId()))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentTrack?.subtitle)
                    //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentTrack?.title)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, exoPlayer.duration) //тут по идее уже надо бы проинициализировать трек
                    .build()

            mediaSession?.apply {
                setMetadata(metadata)
                isActive = true
                setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
            }

            play()
        }

        override fun onPause() {
            pause()

            mediaSession?.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
        }

        override fun onStop() {
            stop()

            mediaSession?.apply {
                isActive = false
                setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F).build())
            }

        }

        override fun onSkipToNext() {
            next()
        }

        override fun onSkipToPrevious() {
            prev()
        }

    }

    private lateinit var exoPlayer: SimpleExoPlayer

    private var _state: Int = IPlayer.STATE_IDLE
    private var _playlist: List<AudioTrack> = ArrayList()
    private var _currentTrackIndex = 0

    override val state: Int get() = _state

    override var playlist: List<AudioTrack>
        get() = _playlist
        set(value) {
            _playlist = value
            _currentTrackIndex = 0
            initCurrentTrack()
        }

    override val currentTrackIndex: Int get() = _currentTrackIndex
    override val currentTrack: AudioTrack? get() = playlist.getOrNull(currentTrackIndex)
    override var autoplay = false
    override val hasNext get() = !playlist.isEmpty() && currentTrackIndex < playlist.size - 1
    override val hasPrev get() = !playlist.isEmpty() && currentTrackIndex > 0

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
                when (playbackState) {
                    Player.STATE_IDLE -> _state = IPlayer.STATE_IDLE
                    Player.STATE_BUFFERING ->
                        if (!playWhenReady) {
                            _state = IPlayer.STATE_PAUSED
                        }
                    Player.STATE_READY ->
                        _state = if (playWhenReady) IPlayer.STATE_PLAYING else IPlayer.STATE_PAUSED
                    Player.STATE_ENDED -> {
                        /*when {
                            playWhenReady && autoplay -> next()
                            else -> changeState(IPlayerModel.STATE_IDLE)
                        }*/
                    }
                }

                var stateString = ""
                when (state) {
                    IPlayer.STATE_IDLE -> {
                        stateString = "STATE_IDLE"
                    }
                    IPlayer.STATE_PLAYING -> {
                        stateString = "STATE_PLAYING"
                    }
                    IPlayer.STATE_PAUSED -> {
                        stateString = "STATE_PAUSED"
                    }
                }

                Log.d("MusicService", "changed _state to $stateString")
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

    override fun changeTrack(index: Int) {
        if (index == currentTrackIndex) {
            return
        }
        if (index in 0 until playlist.size) {
            _currentTrackIndex = index
        }
        initCurrentTrack()
    }

    override fun play() {
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun next() {
        if (hasNext) {
            changeTrack(currentTrackIndex + 1)
            play()
        }
    }

    override fun prev() {
         if (hasPrev) {
             changeTrack(currentTrackIndex - 1)
             play()
         }
    }

    private fun initCurrentTrack() {
        currentTrack?.let {
            val mediaSource = ExtractorMediaSource(
                    Uri.parse(it.url),
                    DefaultDataSourceFactory(applicationContext, "user-agent"),
                    DefaultExtractorsFactory(),
                    null,
                    null)
            exoPlayer.prepare(mediaSource)
        }
    }
}