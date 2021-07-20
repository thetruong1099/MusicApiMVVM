package com.example.musicapimvvm.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapimvvm.R
import com.example.musicapimvvm.model.Song
import kotlinx.android.synthetic.main.recycler_view_audio_item.view.*

class ListSongApiAdapter(
    private val context: Context,
    private val onclick: (Song) -> Unit
) : RecyclerView.Adapter<ListSongApiAdapter.ViewHolder>() {

    private var listSong: MutableList<Song> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.tv_name_song_rv
        val tvArtist = itemView.tv_artist_rv
        val imgThumb = itemView.img_thumb

        fun onBind(musicModel: Song) {
            tvName.text = musicModel.name
            if (musicModel.artist == null) {
                tvArtist.text = "Unknown Artist"

            } else {
                tvArtist.text = "${musicModel.artist}"

            }

            if (musicModel.thumbnail == null) {
                imgThumb.setImageResource(R.drawable.note_music)
            } else {
                Glide.with(context).load(musicModel.thumbnail).into(imgThumb)
            }

            itemView.setOnClickListener { onclick(musicModel) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_audio_item, parent, false)
        return ViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listSong[position])
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    fun setListSong(listSong: MutableList<Song>) {
        this.listSong = listSong
        notifyDataSetChanged()
    }

    fun clearListSong() {
        listSong.clear()
        notifyDataSetChanged()
    }

}