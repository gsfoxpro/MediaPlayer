package com.gsfoxpro.musicplayer

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v7.app.AppCompatActivity
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
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
            val playlist = ArrayList<AudioTrack>().apply {
                add(AudioTrack("file:///android_asset/audio/1.mp3", "AudioTrack Title 1", "AudioTrack Subtitle 1", "https://www.ctclove.ru/upload/iblock/ed3/ed3f06615adace6dbff959b6d84b84ce.jpg"))
                add(AudioTrack("file:///android_asset/audio/2.mp3", "AudioTrack Title 2", "AudioTrack Subtitle 2", "http://marmazov.ru/wp-content/uploads/2017/05/kotiki.jpg"))
                add(AudioTrack("file:///android_asset/audio/3.mp3", "AudioTrack Title 3", "AudioTrack Subtitle 3", "http://kaifolog.ru/uploads/posts/2014-03/thumbs/1396231060_020.jpg"))
                add(AudioTrack("file:///android_asset/audio/4.mp3", "AudioTrack Title 4", "AudioTrack Subtitle 4"))
            }
            musicService?.musicRepo = MusicRepo(playlist)
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
