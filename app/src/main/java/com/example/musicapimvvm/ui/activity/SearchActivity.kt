package com.example.musicapimvvm.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.model.api.MusicObject3
import com.example.musicapimvvm.model.api.SongApi
import com.example.musicapimvvm.ui.adapter.ListSongApiAdapter
import com.example.musicapimvvm.utils.Status
import com.example.musicapimvvm.viewModel.MusicViewModel
import com.example.musicapimvvm.viewModel.NetworkConnectionViewModel
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private val currentData = CurrentData.instance

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

    private val listSongAdapter: ListSongApiAdapter by lazy {
        ListSongApiAdapter(this, onItemClick)
    }

    private val onItemClick: (song: Song) -> Unit = { song ->
        currentData.clearCurrentSongList()
        currentData.addSongToCurrentSongList(song)
        currentData.setStatusOnOff(true)
        addRelateSong(song.idSong)
        val intent = Intent(this, MusicPlayerActivity::class.java)
        intent.putExtra("music", song)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stationary)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        btn_back_search.setOnClickListener { onBackPressed() }

        checkNetworkConnection()

    }

    private fun checkNetworkConnection() {

        networkConnectionViewModel.getNetworkConnection()
            .observe(this) { isConnected ->

                currentData.setCheckInternet(isConnected)

                if (isConnected) {
                    rv_music_search.visibility = View.VISIBLE
                    tv_no_connected.visibility = View.GONE
                    refreshData()
                } else {
                    rv_music_search.visibility = View.GONE
                    tv_no_connected.visibility = View.VISIBLE
                }
            }
    }

    private fun refreshData() {
        rv_music_search.apply {
            adapter = listSongAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listSongAdapter.clearListSong()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    val keyword = s.toString().lowercase()

                    searchMusic(keyword)

                } else listSongAdapter.clearListSong()

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun searchMusic(keyword: String) {
        musicViewModel.searchSong("artist,song,key,code", 500, keyword)
            .observe(this@SearchActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            lav_loading.visibility = View.GONE
                            resource.data?.let { musicObject ->
                                convertToListSong(musicObject)
                            }
                        }
                        Status.ERROR -> {
                            lav_loading.visibility = View.GONE
                            Toast.makeText(
                                this@SearchActivity,
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("aaaa", "refreshData: ${it.message}")
                        }
                        Status.LOADING -> {
                            lav_loading.visibility = View.VISIBLE
                        }
                    }
                }
            }
    }

    private fun convertToListSong(musicObject: MusicObject3) {
        for (i in musicObject.data) {
            val musicList: MutableList<Song> = mutableListOf()
            for (s in i.songs) {
                val song = Song(
                    s.idSong,
                    s.name,
                    s.artist,
                    null,
                    null,
                    "https://photo-resize-zmp3.zadn.vn/w320_r1x1_png/${s.thumb}",
                    "http://api.mp3.zing.vn/api/streaming/audio/${s.idSong}/128",
                    null
                )
                musicList.add(song)
            }
            listSongAdapter.setListSong(musicList)
        }
    }

    private fun addRelateSong(idSong: String) {
        musicViewModel.getListRelatedSong("audio", idSong!!).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { musicObject ->
                            musicObject.data.items?.let { listSong ->
                                convertToListSong(listSong)
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d("aaaa", "refreshData: ${it.message}")
                    }
                    Status.LOADING -> {
                    }
                }

            }
        }
    }

    private fun convertToListSong(listSong: MutableList<SongApi>) {
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
        currentData.addAllToCurrentSongList(musicList)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}