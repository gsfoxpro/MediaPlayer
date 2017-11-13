package com.gsfoxpro.musicservice

import com.gsfoxpro.musicservice.model.AudioTrack

interface IPlayer {

    companion object {
        val STATE_IDLE = 1
        val STATE_PLAYING = 2
        val STATE_PAUSED = 3
    }

    interface Listener {

    }

    val state: Int
    var playlist: List<AudioTrack>
    val currentTrackIndex: Int
    val currentTrack: AudioTrack?

    var autoplay : Boolean
    val hasNext: Boolean
    val hasPrev: Boolean

    fun changeTrack(index: Int)

    fun play()

    fun pause()

    fun stop()

    fun next()

    fun prev()
}