package com.example.musicapimvvm.model.api

import com.google.gson.annotations.SerializedName

data class MusicObject3(
    @SerializedName("data")
    var data: MutableList<MusicDataSearch>
)