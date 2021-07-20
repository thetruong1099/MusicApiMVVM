package com.example.musicapimvvm.model.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_download_table")
data class SongDownloadTable(
    @ColumnInfo(name = "song_id_col") var idSong: String,
    @ColumnInfo(name = "name_song_col") var nameSong: String,
    @ColumnInfo(name = "artist_col") var artist: String,
    @ColumnInfo(name = "album_col") var album: String?,
    @ColumnInfo(name = "genre_col") var genres: String?,
    @ColumnInfo(name = "url_col") var urlSong: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_col")
    var id: Int = 0
}
