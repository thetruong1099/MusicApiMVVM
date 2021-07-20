package com.example.musicapimvvm.currentData

import androidx.lifecycle.MutableLiveData
import com.example.musicapimvvm.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentData {

    companion object {
        val instance = CurrentData()
    }

    private var currentSongList = mutableListOf<Song>()

    fun setCurrentSongList(currentSongs: MutableList<Song>) {
        CoroutineScope(Dispatchers.IO).launch {
            currentSongList = currentSongs
        }
    }

    fun clearCurrentSongList() {
        currentSongList.clear()
    }

    fun addSongToCurrentSongList(song: Song) {
        currentSongList.add(song)
    }

    fun addAllToCurrentSongList(songList: MutableList<Song>) {
        currentSongList.addAll(songList)
    }

    fun getCurrentSongList(): MutableList<Song> = currentSongList

    private var currentSongPos: Int = 0

    fun setCurrentSongPos(currentSongPos: Int) {
        this.currentSongPos = currentSongPos
    }

    fun getCurrentSongPos() = currentSongPos

    private var currentShuffle: Int = 2

    fun setCurrentShuffle(currentShuffle: Int) {
        this.currentShuffle = currentShuffle
    }

    fun getCurrentShuffle(): Int = currentShuffle

    private var currentId: String? = null

    fun setCurrentId(currentId: String) {
        this.currentId = currentId
    }

    fun getCurrentId(): String? = currentId

    private var statusOnOff: Boolean = false

    fun setStatusOnOff(statusOnOff: Boolean) {
        this.statusOnOff = statusOnOff
    }

    fun getStatusOnOff(): Boolean = statusOnOff

    fun getSizeList(): Int = currentSongList.size

    private var checkInternet: Boolean = false

    fun setCheckInternet(checkInternet: Boolean) {
        this.checkInternet = checkInternet
    }

    fun getCheckInternet(): Boolean = checkInternet

    private val currentSongLiveData = MutableLiveData<Song>()

    fun setCurrentSongLiveData(currentSong: Song) {
        currentSongLiveData.value = currentSong
    }

    fun getCurrentSongLiveData(): MutableLiveData<Song> = currentSongLiveData

    fun getCurrentSong(pos: Int): Song = currentSongList[pos]

    fun getCurrentSong(): Song = currentSongList[currentSongPos]

}