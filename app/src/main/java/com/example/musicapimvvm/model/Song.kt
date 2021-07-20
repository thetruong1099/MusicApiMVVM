package com.example.musicapimvvm.model

import java.io.Serializable

data class Song(
    var idSong: String,
    var name: String,
    var artist: String,
    var album: String?,
    var genres: String?,
    var thumbnail: String?,
    var url: String,
    var code:String?
) : Serializable
