package com.example.musicapimvvm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.model.database.SongFavoriteTable
import com.example.musicapimvvm.ui.activity.MusicPlayerActivity
import com.example.musicapimvvm.ui.adapter.ListSongApiAdapter
import com.example.musicapimvvm.viewModel.MusicViewModel
import com.example.musicapimvvm.viewModel.NetworkConnectionViewModel
import com.example.mydiary.util.CustomBackStackFragment
import kotlinx.android.synthetic.main.fragment_person.*


class PersonFragment : CustomBackStackFragment() {

    private val currentData = CurrentData.instance

    private var tempListSong: MutableList<Song> = mutableListOf()

    private val musicViewModel by lazy {
        ViewModelProvider(
            this,
            MusicViewModel.MusicViewModelFactory(requireActivity().application)
        )[MusicViewModel::class.java]
    }

    private val networkConnectionViewModel by lazy {
        ViewModelProvider(
            this,
            NetworkConnectionViewModel.NetworkConnectionViewModelFactory(requireActivity().application)
        )[NetworkConnectionViewModel::class.java]
    }

    private val listSongAdapter by lazy {
        ListSongApiAdapter(requireContext(), onItemClick)
    }

    private val onItemClick: (song: Song) -> Unit = { song ->
        if (currentData.getCheckInternet()) {
            currentData.setCurrentSongList(tempListSong)
            currentData.setStatusOnOff(true)
            currentData.setCurrentSongPos(tempListSong.indexOf(song))
            currentData.setCurrentSongLiveData(song)
            val intent = Intent(requireContext(), MusicPlayerActivity::class.java)
            intent.putExtra("music", song)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stationary)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_favorite_song.apply {
            adapter = listSongAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        refreshData()

        checkNetworkConnection()

    }

    private fun refreshData() {
        musicViewModel.getFavoriteMusic().observe(viewLifecycleOwner) { musicList ->
            val songList: MutableList<Song> = convertToListSong(musicList)
            listSongAdapter.setListSong(songList)
            tempListSong = songList
        }
    }

    private fun convertToListSong(musicList: MutableList<SongFavoriteTable>): MutableList<Song> {
        val songList: MutableList<Song> = mutableListOf()
        for (i in musicList) {
            val song = Song(
                i.idSong,
                i.nameSong,
                i.artist,
                null,
                null,
                i.thumbnail,
                "http://api.mp3.zing.vn/api/streaming/audio/${i.idSong}/128",
                i.code
            )
            songList.add(song)
        }
        return songList
    }

    private fun checkNetworkConnection() {
        networkConnectionViewModel.getNetworkConnection()
            .observe(viewLifecycleOwner) { isConnected ->
                currentData.setCheckInternet(isConnected)
            }
    }

}