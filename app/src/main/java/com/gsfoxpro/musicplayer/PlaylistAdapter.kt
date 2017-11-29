package com.gsfoxpro.musicplayer

import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.model.AudioTrack
import kotlinx.android.synthetic.main.playlist_item.view.*

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistItemViewHolder>() {

    var data: ArrayList<AudioTrack> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var mediaSession: MediaSessionCompat? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistItemViewHolder {
        val playlistItemView = LayoutInflater.from(parent?.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistItemViewHolder(playlistItemView)
    }

    override fun onBindViewHolder(holder: PlaylistItemViewHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount() = data.size


    inner class PlaylistItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var id: Int = -1

        fun update(audioTrack: AudioTrack) {
            this.id = id
            itemView.apply {
                title_textview.text = audioTrack.title
                subtitle_textview.text = audioTrack.subtitle
                music_player_button.audioTrack = audioTrack
                music_player_button.mediaSession = mediaSession
                Glide.with(context)
                        .load(audioTrack.imageUrl)
                        .into(image_view)
            }
        }
    }
}