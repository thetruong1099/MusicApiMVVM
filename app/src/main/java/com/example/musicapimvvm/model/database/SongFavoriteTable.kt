package com.example.musicapimvvm.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_favorite_table")
data class SongFavoriteTable(
    @ColumnInfo(name = "song_id_col") var idSong: String,
    @ColumnInfo(name = "name_song_col") var nameSong: String,
    @ColumnInfo(name = "artist_col") var artist: String,
    @ColumnInfo(name = "thumbnail_col") var thumbnail: String?,
    @ColumnInfo(name = "favorite_col") var statusFavorite: Boolean,
    @ColumnInfo(name = "code_col") var code: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_col")
    var id: Int = 0
}
