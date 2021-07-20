package com.example.musicapimvvm.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.ui.activity.MusicPlayerActivity
import com.example.musicapimvvm.ui.adapter.ListSongDbAdapter
import com.example.musicapimvvm.viewModel.MusicViewModel
import com.example.mydiary.util.CustomBackStackFragment
import kotlinx.android.synthetic.main.fragment_library.*


class LibraryFragment : CustomBackStackFragment() {

    private val currentData = CurrentData.instance

    private var tempListSong: MutableList<Song> = mutableListOf()

    private val musicViewModel by lazy {
        ViewModelProvider(
            this,
            MusicViewModel.MusicViewModelFactory(requireActivity().application)
        )[MusicViewModel::class.java]
    }

    private val listSongAdapter by lazy {
        ListSongDbAdapter(onItemClick)
    }

    private val onItemClick: (song: Song) -> Unit = { song ->
        currentData.setCurrentSongList(tempListSong)
        currentData.setStatusOnOff(false)
        currentData.setCurrentSongPos(tempListSong.indexOf(song))
        currentData.setCurrentSongLiveData(song)
        var intent = Intent(requireContext(), MusicPlayerActivity::class.java)
        intent.putExtra("music", song)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stationary)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_music_downloaded.apply {
            adapter = listSongAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        musicViewModel.getAllMusicDownload().observe(viewLifecycleOwner) { musicList ->
            val songList: MutableList<Song> = mutableListOf()
            for (i in musicList) {
                val song = Song(
                    i.idSong,
                    i.nameSong,
                    i.artist,
                    i.album,
                    i.genres,
                    null,
                    i.urlSong,
                    null
                )
                songList.add(song)
            }
            listSongAdapter.setListSong(songList)
            tempListSong = songList
        }

    }

}