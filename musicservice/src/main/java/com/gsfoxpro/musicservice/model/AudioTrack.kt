package com.gsfoxpro.musicservice.model

open class AudioTrack(
        open val id: Long,
        open val url: String,
        open val title: String? = null,
        open val subtitle: String? = null,
        open val imageUrl: String? = null
)