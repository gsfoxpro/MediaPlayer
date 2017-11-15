package com.gsfoxpro.musicservice

import com.gsfoxpro.musicservice.model.AudioTrack

class MusicRepo {

    private var playlist = ArrayList<AudioTrack>().apply {
        add(AudioTrack("file:///android_asset/audio/1.mp3", "AudioTrack Title 1", "AudioTrack Subtitle 1", "https://www.ctclove.ru/upload/iblock/ed3/ed3f06615adace6dbff959b6d84b84ce.jpg"))
        add(AudioTrack("file:///android_asset/audio/2.mp3", "AudioTrack Title 2", "AudioTrack Subtitle 2", "http://marmazov.ru/wp-content/uploads/2017/05/kotiki.jpg"))
        add(AudioTrack("file:///android_asset/audio/3.mp3", "AudioTrack Title 3", "AudioTrack Subtitle 3", "http://kaifolog.ru/uploads/posts/2014-03/thumbs/1396231060_020.jpg"))
        add(AudioTrack("file:///android_asset/audio/4.mp3", "AudioTrack Title 4", "AudioTrack Subtitle 4"))
    }

    private var currentTrackIndex = 0
    private val hasNext get() = !playlist.isEmpty() && currentTrackIndex < playlist.size - 1
    private val hasPrev get() = !playlist.isEmpty() && currentTrackIndex > 0


    val currentAudioTrack: AudioTrack? = getAudioTrackAtIndex(currentTrackIndex)

    val nextAudioTrack: AudioTrack?
        get() = when (hasNext) {
            true -> getAudioTrackAtIndex(++currentTrackIndex)
            else -> null
        }

    val prevAudioTrack: AudioTrack?
        get() = when (hasPrev) {
            true -> getAudioTrackAtIndex(--currentTrackIndex)
            else -> null
        }

    fun getAudioTrackAtIndex(index: Int): AudioTrack? = playlist.getOrNull(index)

    init {
        playlist.apply {  }
    }

}