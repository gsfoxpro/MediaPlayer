package com.gsfoxpro.musicplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.gsfoxpro.musicservice.IPlayer
import com.gsfoxpro.musicservice.service.MusicService

class App : Application() {

    private var musicService: MusicService? = null

    val player: IPlayer?
        get() = musicService

    val mediaSessionToken: MediaSessionCompat.Token?
        get() = musicService?.mediaSessionToken

    private var isMusicServiceBound: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            musicService = (binder as MusicService.LocalBinder).musicService
            isMusicServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isMusicServiceBound = false
        }
    }

    override fun onCreate() {
        super.onCreate()
        bindMusicService()
    }

    private fun bindMusicService() {
        startService(Intent(this, MusicService::class.java))
        bindService(Intent(applicationContext, MusicService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }
}