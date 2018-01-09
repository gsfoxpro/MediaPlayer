package com.gsfoxpro.musicservice.ui

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.gsfoxpro.musicservice.R
import com.gsfoxpro.musicservice.model.AudioTrack
import kotlinx.android.synthetic.main.music_player_button.view.*

class MusicPlayerButton : MusicPlayer {

    var audioTrack: AudioTrack? = null

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player_button, this)

        play_pause_button.setOnClickListener {
            if (mediaController?.metadata?.description?.mediaUri.toString() == audioTrack?.url)  {
                playPause()
                return@setOnClickListener
            }
            audioTrack?.id?.let { id ->
                skipToQueueItem(id)
            }
        }
    }

    override fun updateButtonsStates() {
        when (playing && mediaController?.metadata?.description?.mediaUri.toString() == audioTrack?.url) {
            true -> play_pause_button.setImageResource(R.drawable.ic_pause_circle)
            else -> play_pause_button.setImageResource(R.drawable.ic_play_circle)
        }
    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
    }

    override fun updateDuration(durationMs: Long) {
    }

    override fun updateCurrentPosition(positionMs: Long) {
    }
}