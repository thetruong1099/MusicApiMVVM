package com.example.musicapimvvm.repository

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.musicapimvvm.api.ApiConfig
import com.example.musicapimvvm.database.MusicDatabase
import com.example.musicapimvvm.database.dao.MusicDAO
import com.example.musicapimvvm.model.database.SongDownloadTable
import com.example.musicapimvvm.model.database.SongFavoriteTable
import kotlinx.android.synthetic.main.activity_music_player.*
import okhttp3.ResponseBody
import java.io.*

class MusicRepository(app: Application) {

    //api

    suspend fun getListMusicRank(
        songId: Int,
        videoId: Int,
        albumId: Int,
        chart: String,
        time: Int
    ) = ApiConfig.apiService.getListMusicRank(songId, videoId, albumId, chart, time)

    suspend fun getListRelatedSong(type: String, id: String) =
        ApiConfig.apiService.getListRelatedSong(type, id)

    suspend fun getInfoSong(type: String, id: String) = ApiConfig.apiService.getInfoSong(type, id)

    suspend fun downloadFile(url: String) = ApiConfig.apiService2.downloadFileByUrl(url)

    suspend fun searchSong(type: String, num: Int, query: String) =
        ApiConfig.apiService3.searchSong(type, num, query)

    suspend fun getListMusicUSUK(
        op: String,
        id: String
    ) = ApiConfig.apiService.getListMusicUSUK(op, id)

    suspend fun getInfoAlbum(type: String, key: String) =
        ApiConfig.apiService.getInfoAlbum(type, key)

    //database

    private val musicDAO: MusicDAO

    init {
        val musicDatabase: MusicDatabase = MusicDatabase.getInstance(app)
        musicDAO = musicDatabase.getMusicDao()
    }

    suspend fun insertMusicDownload(song: SongDownloadTable) = musicDAO.insertMusicDownload(song)

    fun getAllMusicDownload(): LiveData<MutableList<SongDownloadTable>> =
        musicDAO.getAllMusicDownload()

    suspend fun insertFavoriteSong(song: SongFavoriteTable) = musicDAO.insertFavoriteSong(song)

    suspend fun deleteFavoriteSong(songId: String) = musicDAO.deleteFavoriteSong(songId)

    fun getStatusFavorite(songId: String): LiveData<Boolean> = musicDAO.getStatusFavorite(songId)

    fun getFavoriteMusic(): LiveData<MutableList<SongFavoriteTable>> = musicDAO.getFavoriteMusic()

    //file

    suspend fun writeFile(
        body: ResponseBody,
        idSong: String,
        packageName: String,
        context: Context
    ){
        try {
            File("/data/data/$packageName/music").mkdir()
            val destinationFile = File("/data/data/$packageName/music/$idSong.mp3")

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(destinationFile)

                while (true) {
                    val read = inputStream.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read

                }

                outputStream.flush();

                insertToDataBase(destinationFile, idSong, packageName, context)

            } catch (e: IOException) {
                Log.d("aaaa", "Failed to save the file! ${e.message}");
            } finally {
                inputStream?.let { inputStream.close() }

                outputStream?.let { outputStream.close() }
            }

        } catch (e: IOException) {
            Log.d("aaaa", "Failed to save the file! ${e.message}")
        }
    }

    private suspend fun insertToDataBase(
        file: File,
        idSong: String,
        packageName: String,
        context: Context
    ) {
        val uri = Uri.fromFile(file)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        val genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
        val urlSong = "/data/data/$packageName/music/$idSong.mp3"

        //insert to database
        insertMusicDownload(
            SongDownloadTable(
                idSong,
                name!!,
                artist!!,
                album,
                genre,
                urlSong
            )
        )
    }
}