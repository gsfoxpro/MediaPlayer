package com.gsfoxpro.musicplayer

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.app.AppCompatActivity
import com.gsfoxpro.musicservice.service.MusicService
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {

    private var mediaController: MediaControllerCompat? = null

    private val musicService: MusicService?
        get() = (application as App).musicService

    private val mediaSessionToken: MediaSessionCompat.Token?
        get() = (application as App).mediaSessionToken


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        play_button.setOnClickListener {
            mediaSessionToken?.let {
                mediaController = MediaControllerCompat(this, it)
            }
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
