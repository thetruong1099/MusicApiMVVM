package com.example.musicapimvvm.model.api

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("name")
    var name: String?,
)
