package com.example.musicapimvvm.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiConfig {
    //get chart song
    private const val BASE_URL = "https://mp3.zing.vn"
    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    val retrofit = builder.build()
    val apiService: MusicApi = retrofit.create(MusicApi::class.java)

    //download song
    private const val BASE_URL_2 = "http://api.mp3.zing.vn"
    private val httpClient = OkHttpClient.Builder()
    private val builder2 = Retrofit.Builder()
        .baseUrl(BASE_URL_2)
        .addConverterFactory(GsonConverterFactory.create())

    val retrofit2 = builder2.client(httpClient.build()).build()
    val apiService2: MusicApi = retrofit2.create(MusicApi::class.java)

    //search song
    private const val BASE_URL_3 = "http://ac.mp3.zing.vn"
    private val builder3 =
        Retrofit.Builder().baseUrl(BASE_URL_3).addConverterFactory(GsonConverterFactory.create())
    val retrofit3 = builder3.build()
    val apiService3: MusicApi = retrofit3.create(MusicApi::class.java)

}