package com.gsfoxpro.musicservice.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import com.gsfoxpro.musicservice.R
import com.gsfoxpro.musicservice.service.MusicService
import kotlinx.android.synthetic.main.music_player.view.*
import java.util.concurrent.TimeUnit


open class MusicPlayer : FrameLayout {

    val TAG = "MusicPlayer"

    var mediaSession: MediaSessionCompat? = null
        set(value) {
            field = value
            registerCallback()
        }

    private val mediaController: MediaControllerCompat?
        get() = mediaSession?.controller

    private val playing
        get() = mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING

    private var callbackRegistered = false
    private var needUpdateProgress = false

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            updateUI()
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            updateUI()
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                MusicService.PROGRESS_UPDATE_EVENT -> {
                    val progress = extras?.getLong(MusicService.CURRENT_PROGRESS) ?: 0L
                    if (progress >= 0 && needUpdateProgress) {
                        current_position_textview.text = getUserFriendlyTime(progress)
                        seek_bar.progress = progress.toInt()
                    }
                }
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player, this)

        play_pause_button.setOnClickListener {
            mediaController?.let { controller ->
                when (playing) {
                    true -> controller.transportControls.pause()
                    else -> controller.transportControls.play()
                }
            }
        }

        next_button.setOnClickListener {
            mediaController?.transportControls?.skipToNext()
        }

        prev_button.setOnClickListener {
            mediaController?.transportControls?.skipToPrevious()
        }

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                current_position_textview.text = getUserFriendlyTime(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                needUpdateProgress = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.toLong()?.let { progressMs ->
                    mediaController?.transportControls?.seekTo(progressMs)
                    needUpdateProgress = true
                }
            }

        })

        registerCallback()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerCallback()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediaController?.unregisterCallback(mediaControllerCallback)
    }

    private fun updateUI() {
        when (playing) {
            true -> play_pause_button.setImageResource(R.drawable.ic_pause)
            else -> play_pause_button.setImageResource(R.drawable.ic_play_arrow)
        }
        val data = mediaController?.metadata
        val title = data?.description?.title
        val subtitle = data?.description?.subtitle
        Log.d(TAG, "title: $title")
        Log.d(TAG, "subtitle: $subtitle")
        data?.getLong(METADATA_KEY_DURATION)?.let { duration ->
            if (duration >= 0) {
                if (seek_bar.max != duration.toInt()) {
                    seek_bar.progress = 0
                }
                seek_bar.max = duration.toInt()
                duration_textview.text = getUserFriendlyTime(duration)
            }
        }
        needUpdateProgress = playing
    }

    private fun registerCallback() {
        if (callbackRegistered) {
            return
        }
        mediaController?.apply {
            registerCallback(mediaControllerCallback)
            callbackRegistered = true
        }
    }

    private fun getUserFriendlyTime(timeMs: Long): String {
        if (timeMs < 0) {
            return "0:00"
        }
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60
        if (seconds < 10) {
            return "${TimeUnit.MILLISECONDS.toMinutes(timeMs)}:0$seconds"
        }

        return "${TimeUnit.MILLISECONDS.toMinutes(timeMs)}:$seconds"
    }

}