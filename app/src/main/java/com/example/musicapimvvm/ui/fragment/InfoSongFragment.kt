package com.example.musicapimvvm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.utils.Status
import com.example.musicapimvvm.viewModel.MusicViewModel
import kotlinx.android.synthetic.main.fragment_info_song.*
import java.io.File


class InfoSongFragment : Fragment() {

    private val currentData = CurrentData.instance

    private val musicViewModel by lazy {
        ViewModelProvider(
            this,
            MusicViewModel.MusicViewModelFactory(requireActivity().application)
        )[MusicViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val song = currentData.getCurrentSong()
        updateUiAfterListenChange()
    }

    private fun updateUiAfterListenChange() {
        currentData.getCurrentSongLiveData().observe(viewLifecycleOwner) {
            updateUi(it)
        }
    }

    private fun updateUi(song: Song) {

        if (currentData.getStatusOnOff()) {
            updateUiForOnline(song)
        } else {
            updateUIForOffline(song)
        }

        tv_name_song.text = song.name

        if (song.artist == null) {
            tv_artist.text = "Unknow Artist"
        } else {
            tv_artist.text = song.artist
        }
    }

    private fun updateUIForOffline(song: Song) {
        try {
            val thumbnail = android.media.ThumbnailUtils.createAudioThumbnail(
                File(song.url),
                Size(320, 320),
                null
            )
            if (thumbnail == null) {
                img_song.setImageResource(R.drawable.note_music)
            } else {
                img_song.setImageBitmap(thumbnail)
            }
        } catch (e: Exception) {
            img_song.setImageResource(R.drawable.note_music)
            Log.d("aaaa", "updateUIForOffline: $e")
        }
        if (song.genres == null) {
            tv_genres.text = "Unknow Genres"
        } else tv_genres.text = song.genres

        if (song.album == null) {
            tv_album_name.text = "Unknow Album"
        } else {
            tv_album_name.text = song.album
        }
    }

    private fun updateUiForOnline(song: Song) {
        if (song.thumbnail == null) {
            img_song.setImageResource(R.drawable.note_music)
        } else {
            Glide.with(requireContext()).load(song.thumbnail).into(img_song)
        }

        setUiForGeneres(song.idSong)

        setUiForAlbum(song.code)
    }

    private fun setUiForGeneres(idSong: String) {
        musicViewModel.getInfoSong("audio", idSong).observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        lav_loader.visibility = View.GONE
                        resource.data?.let { musicObject ->
                            var string: String = ""
                            for (i in musicObject.song.genres) {
                                string = i.name + " " + string
                            }
                            tv_genres.text = string.trim()
                        }
                    }
                    Status.ERROR -> {
                        lav_loader.visibility = View.GONE
                        Log.d("aaaa", "refreshData: ${it.message}")
                    }
                    Status.LOADING -> {
                        lav_loader.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setUiForAlbum(code: String?) {
        if (code != null) {
            musicViewModel.getInfoAlbum("audio", code!!).observe(viewLifecycleOwner) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            lav_loader.visibility = View.GONE
                            resource.data?.let { musicObject ->
                                tv_album_name.text = musicObject.song.album?.name
                            }
                        }
                        Status.ERROR -> {
                            lav_loader.visibility = View.GONE
                            Log.d("aaaa", "refreshData: ${it.message}")
                        }
                        Status.LOADING -> {
                            lav_loader.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } else {
            tv_album_name.text = "Unknow Album"
        }
    }
}