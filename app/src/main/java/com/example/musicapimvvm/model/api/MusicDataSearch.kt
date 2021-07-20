package com.example.musicapimvvm.model.api

import com.google.gson.annotations.SerializedName

data class MusicDataSearch(
    @SerializedName("song")
    var songs: MutableList<SongSearch>
)