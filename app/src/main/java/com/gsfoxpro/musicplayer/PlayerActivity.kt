package com.gsfoxpro.musicplayer

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.app.AppCompatActivity
import com.gsfoxpro.musicservice.IPlayer
import com.gsfoxpro.musicservice.model.AudioTrack
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {

    private var mediaController: MediaControllerCompat? = null

    private val player: IPlayer?
        get() = (application as App).player

    private val mediaSessionToken: MediaSessionCompat.Token?
        get() = (application as App).mediaSessionToken

    private var playlistInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        play_button.setOnClickListener {
            if (!playlistInitialized) {
                player?.playlist = ArrayList<AudioTrack>().apply {
                    add(AudioTrack("file:///android_asset/audio/1.mp3", "AudioTrack Title 1", "AudioTrack Subtitle 1"))
                    add(AudioTrack("file:///android_asset/audio/2.mp3", "AudioTrack Title 2", "AudioTrack Subtitle 2"))
                    add(AudioTrack("file:///android_asset/audio/3.mp3", "AudioTrack Title 3", "AudioTrack Subtitle 3"))
                    add(AudioTrack("file:///android_asset/audio/4.mp3", "AudioTrack Title 4", "AudioTrack Subtitle 4"))
                }

                mediaSessionToken?.let {
                    mediaController = MediaControllerCompat(this, it)
                }

                playlistInitialized = true
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
            player?.next()
        }

        prev_button.setOnClickListener {
            player?.prev()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
