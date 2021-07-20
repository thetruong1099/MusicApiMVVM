package com.example.musicapimvvm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.model.api.SongApi
import com.example.musicapimvvm.ui.adapter.ListSongApiAdapter
import com.example.musicapimvvm.utils.Status
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.ui.activity.MusicPlayerActivity
import com.example.musicapimvvm.viewModel.MusicViewModel
import com.example.musicapimvvm.viewModel.NetworkConnectionViewModel
import kotlinx.android.synthetic.main.fragment_music_u_s_u_k.*


class MusicUSUKFragment : Fragment() {

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
        currentData.setCurrentSongList(tempListSong)
        currentData.setStatusOnOff(true)
        val intent = Intent(requireContext(), MusicPlayerActivity::class.java)
        intent.putExtra("music", song)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stationary)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_u_s_u_k, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_music_us_uk.apply {
            adapter = listSongAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        refreshData()

        checkNetworkConnection()

        swipe_layout.setOnRefreshListener {
            refreshData()
        }

    }

    private fun checkNetworkConnection() {
        networkConnectionViewModel.getNetworkConnection()
            .observe(viewLifecycleOwner) { isConnected ->

                currentData.setCheckInternet(isConnected)

                if (isConnected) {
                    tv_connected.visibility = View.GONE
                    swipe_layout.visibility = View.VISIBLE
                    refreshData()
                } else {
                    tv_connected.visibility = View.VISIBLE
                    swipe_layout.visibility = View.GONE
                }
            }
    }

    private fun refreshData() {
        musicViewModel.getListMusicUSUK(
            "top100", "ZWZB96AE"
        ).observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        swipe_layout.isRefreshing = false
                        resource.data?.let { musicObject ->
                            musicObject.data.items.let { listSong ->
                                val musicList: MutableList<Song> = convertToSongList(listSong)
                                listSongAdapter.setListSong(musicList)
                                tempListSong = musicList
                            }

                        }
                    }
                    Status.ERROR -> {
                        swipe_layout.isRefreshing = false
                        Log.d("aaaa", "refreshData: ${it.message}")
                    }
                    Status.LOADING -> {
                        swipe_layout.isRefreshing = true
                    }
                }
            }
        }
    }

    private fun convertToSongList(listSong: MutableList<SongApi>): MutableList<Song> {
        val musicList: MutableList<Song> = mutableListOf()
        for (i in listSong) {
            val song = Song(
                i.idSong,
                i.name,
                i.artists_names,
                i.album?.name,
                null,
                i.thumbnail,
                "http://api.mp3.zing.vn/api/streaming/audio/${i.idSong}/128",
                i.code
            )

            musicList.add(song)
        }
        return musicList
    }

}