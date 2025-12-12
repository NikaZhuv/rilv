package com.example.rilv.watchlists.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rilv.moviewatch.WatchlistMovieDao
import com.example.rilv.watchlists.WatchlistEntity
import com.example.rilv.moviewatch.WatchlistMovieEntity

@Database(
    entities = [
        WatchlistEntity::class,
        WatchlistMovieEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WatchlistDatabase : RoomDatabase() {
    abstract val watchlistDao: WatchlistDao
    abstract val watchlistMovieDao: WatchlistMovieDao
}