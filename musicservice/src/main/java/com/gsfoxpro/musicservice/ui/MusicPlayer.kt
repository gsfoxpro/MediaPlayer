package com.gsfoxpro.musicservice.ui

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.gsfoxpro.musicservice.R
import kotlinx.android.synthetic.main.music_player.view.*


class MusicPlayer : FrameLayout {

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

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updateUI()
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
            true -> play_pause_button.setImageResource(R.drawable.ic_pause_black_24dp)
            else -> play_pause_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
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
}