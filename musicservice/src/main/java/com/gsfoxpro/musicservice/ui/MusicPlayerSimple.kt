package com.gsfoxpro.musicservice.ui

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import com.gsfoxpro.musicservice.R
import kotlinx.android.synthetic.main.music_player_simple.view.*

class MusicPlayerSimple : MusicPlayer {

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player_simple, this)

        play_pause_button.setOnClickListener { playPause() }
        next_button.setOnClickListener { next() }
        prev_button.setOnClickListener { prev() }

        initSeekBar(seek_bar)
    }

    override fun updateButtonsStates() {
        when (playing) {
            true -> play_pause_button.setImageResource(R.drawable.ic_pause)
            else -> play_pause_button.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        val title = metadata.description?.title
        val subtitle = metadata.description?.subtitle
        Log.d(TAG, "title: $title")
        Log.d(TAG, "subtitle: $subtitle")
    }

    override fun updateDuration(durationMs: Long) {
        duration_textview.text = getUserFriendlyTime(durationMs)
    }

    override fun updateCurrentPosition(positionMs: Long) {
        current_position_textview.text = getUserFriendlyTime(positionMs)
    }
}