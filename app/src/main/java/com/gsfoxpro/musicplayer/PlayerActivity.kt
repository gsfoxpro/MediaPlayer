package com.gsfoxpro.musicplayer

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v7.app.AppCompatActivity
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.service.MusicService
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {

    private val mediaController: MediaControllerCompat?
        get() = (application as App).musicService?.mediaSession?.controller

    private val musicService: MusicService?
        get() = (application as App).musicService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        if (musicService?.musicRepo == null) {
            musicService?.musicRepo = MusicRepo()
        }
        music_player?.mediaSession = musicService?.mediaSession

        play_button.setOnClickListener {
            mediaController?.transportControls?.play()
        }

        pause_button.setOnClickListener {
            mediaController?.transportControls?.pause()
        }

        stop_button.setOnClickListener {
            mediaController?.transportControls?.stop()
        }

        next_button.setOnClickListener {
            mediaController?.transportControls?.skipToNext()
        }

        prev_button.setOnClickListener {
            mediaController?.transportControls?.skipToPrevious()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
