package com.example.musicapimvvm.ui.adapter

import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapimvvm.R
import com.example.musicapimvvm.model.Song
import kotlinx.android.synthetic.main.recycler_view_audio_item.view.*
import java.io.File

class ListSongDbAdapter(
    private val onclick: (Song) -> Unit
) : RecyclerView.Adapter<ListSongDbAdapter.ViewHolder>() {

    private var listSong: MutableList<Song> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.tv_name_song_rv
        val tvArtist = itemView.tv_artist_rv
        val imgThumb = itemView.img_thumb

        fun onBind(song: Song) {
            tvName.text = song.name
            if (song.artist == null) {
                tvArtist.text = "Unknown Artist"

            } else {
                tvArtist.text = "${song.artist}"

            }

            val thumbnail = android.media.ThumbnailUtils.createAudioThumbnail(
                File(song.url),
                Size(320, 320),
                null
            )

            if (thumbnail == null) {
                imgThumb.setImageResource(R.drawable.note_music)
            } else {
                imgThumb.setImageBitmap(thumbnail)
            }

            itemView.setOnClickListener { onclick(song) }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListSongDbAdapter.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_audio_item, parent, false)
        return ViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: ListSongDbAdapter.ViewHolder, position: Int) {
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