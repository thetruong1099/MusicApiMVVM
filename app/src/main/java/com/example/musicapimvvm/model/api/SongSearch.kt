package com.example.musicapimvvm.model.api

import com.google.gson.annotations.SerializedName

data class SongSearch(
    @SerializedName("id")
    var idSong: String = "",
    @SerializedName("name")
    var name: String,
    @SerializedName("artist")
    var artist: String,
    @SerializedName("thumb")
    var thumb: String
)
