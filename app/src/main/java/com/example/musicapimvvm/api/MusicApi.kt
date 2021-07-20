package com.example.musicapimvvm.api

import com.example.musicapimvvm.model.api.MusicObject
import com.example.musicapimvvm.model.api.MusicObject2
import com.example.musicapimvvm.model.api.MusicObject3
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface MusicApi {

    //get chart song
    @GET("/xhr/chart-realtime?")
    suspend fun getListMusicRank(
        @Query("songId") songId: Int,
        @Query("videoId") videoId: Int,
        @Query("albumId") albumId: Int,
        @Query("chart") chart: String,
        @Query("time") time: Int
    ): MusicObject

    //get relate song
    @GET("/xhr/recommend?")
    suspend fun getListRelatedSong(
        @Query("type") type: String,
        @Query("id") id: String
    ): MusicObject

    //get info song
    @GET("/xhr/media/get-info?")
    suspend fun getInfoSong(
        @Query("type") type: String,
        @Query("id") id: String
    ): MusicObject2

    //download song
    @Streaming
    @GET
    suspend fun downloadFileByUrl(@Url fileUrl: String?): ResponseBody

    //get search song
    @GET("/complete?")
    suspend fun searchSong(
        @Query("type") type: String,
        @Query("num") num: Int,
        @Query("query") query: String
    ): MusicObject3

    //get 100 song us uk
    @GET("/xhr/media/get-list?")
    suspend fun getListMusicUSUK(
        @Query("op") op: String,
//        @Query("start") start: Int,
//        @Query("length") length: Int,
        @Query("id") id: String
    ): MusicObject

    @GET("/xhr/media/get-source?")
    suspend fun getInfoAlbum(
        @Query("type") type: String,
        @Query("key") key: String
    ): MusicObject2
}