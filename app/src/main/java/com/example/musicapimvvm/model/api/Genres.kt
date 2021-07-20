package com.example.musicapimvvm.model.api

import com.google.gson.annotations.SerializedName

data class Genres(
    @SerializedName("name")
    var name: String
)