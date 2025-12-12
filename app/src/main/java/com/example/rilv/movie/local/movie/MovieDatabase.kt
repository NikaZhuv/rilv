package com.example.rilv.movie.local.movie

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieEntity::class],
    version = 8
)

abstract class MovieDatabase: RoomDatabase() {
    abstract val movieDao: MovieDao
}
