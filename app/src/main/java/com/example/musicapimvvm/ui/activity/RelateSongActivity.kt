package com.example.musicapimvvm.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.model.api.SongApi
import com.example.musicapimvvm.ui.adapter.ListSongApiAdapter
import com.example.musicapimvvm.utils.Status
import com.example.musicapimvvm.viewModel.MusicViewModel
import com.example.musicapimvvm.viewModel.NetworkConnectionViewModel
import kotlinx.android.synthetic.main.activity_relate_song.*

class RelateSongActivity : AppCompatActivity() {

    private val currentData = CurrentData.instance

    private var tempListSong: MutableList<Song> = mutableListOf()

    private val musicViewModel by lazy {
        ViewModelProvider(
            this,
            MusicViewModel.MusicViewModelFactory(this.application)
        )[MusicViewModel::class.java]
    }

    private val networkConnectionViewModel by lazy {
        ViewModelProvider(
            this,
            NetworkConnectionViewModel.NetworkConnectionViewModelFactory(this.application)
        )[NetworkConnectionViewModel::class.java]
    }

    private val listSongAdapter by lazy {
        ListSongApiAdapter(this, onItemClick)
    }

    private val onItemClick: (song: Song) -> Unit = { song ->
        currentData.setCurrentSongList(tempListSong)
        currentData.setStatusOnOff(true)
        val intent = Intent(this, MusicPlayerActivity::class.java)
        intent.putExtra("music", song)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relate_song)

        btn_clear.setOnClickListener { onBackPressed() }

        checkNetworkConnection()

    }

    private fun checkNetworkConnection() {
        networkConnectionViewModel.getNetworkConnection().observe(this) { isConnected ->
            currentData.setCheckInternet(isConnected)

            if (isConnected) {

                tv_no_connected.visibility = View.GONE
                rv_relate_song.visibility = View.VISIBLE

                refreshData()

            } else {
                tv_no_connected.visibility = View.VISIBLE
                rv_relate_song.visibility = View.GONE
            }

        }
    }

    private fun refreshData() {
        rv_relate_song.apply {
            adapter = listSongAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        currentData.getCurrentId()?.let { id ->
            musicViewModel.getListRelatedSong("audio", id).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            lav_loading.visibility = View.GONE
                            rv_relate_song.visibility = View.VISIBLE
                            resource.data?.let { musicObject ->
                                musicObject.data.items.let { listSong ->
                                    val musicList: MutableList<Song> = convertToListSong(listSong)
                                    listSongAdapter.setListSong(musicList)
                                    tempListSong = musicList
                                }
                            }
                        }
                        Status.ERROR -> {
                            lav_loading.visibility = View.GONE
                            rv_relate_song.visibility = View.VISIBLE
                            Log.d("aaaa", "refreshData: ${it.message}")
                        }
                        Status.LOADING -> {
                            lav_loading.visibility = View.VISIBLE
                            rv_relate_song.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }

    private fun convertToListSong(listSong: MutableList<SongApi>): MutableList<Song> {
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}