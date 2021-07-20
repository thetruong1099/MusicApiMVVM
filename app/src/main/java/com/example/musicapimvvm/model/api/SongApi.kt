package com.example.musicapimvvm.model.api

import com.google.gson.annotations.SerializedName

data class SongApi(
    @SerializedName("id")
    var idSong: String = "",
    @SerializedName("name")
    var name: String,
    @SerializedName("artists_names")
    var artists_names: String,
    @SerializedName("thumbnail")
    var thumbnail: String,
    @SerializedName("album")
    var album: Album?,
    @SerializedName("genres")
    var genres: MutableList<Genres>,
    @SerializedName("code")
    var code:String
)
