package com.gsfoxpro.musicservice

import com.gsfoxpro.musicservice.model.AudioTrack

open class MusicRepo(private val playlist: ArrayList<AudioTrack>) {

    val hasNext get() = !playlist.isEmpty() && currentTrackIndex < playlist.size - 1
    val hasPrev get() = !playlist.isEmpty() && currentTrackIndex > 0

    private var currentTrackIndex = 0

    open var autoPlay = true
    open val currentAudioTrack: AudioTrack?
        get() = getAudioTrackAtIndex(currentTrackIndex)
    open val nextAudioTrack: AudioTrack?
        get() = when (hasNext) {
            true -> getAudioTrackAtIndex(++currentTrackIndex)
            else -> null
        }
    open val prevAudioTrack: AudioTrack?
        get() = when (hasPrev) {
            true -> getAudioTrackAtIndex(--currentTrackIndex)
            else -> null
        }

    private fun getAudioTrackAtIndex(index: Int): AudioTrack? = playlist.getOrNull(index)
}