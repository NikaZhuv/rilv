package com.example.rilv.movie.local.movie

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query

@Dao
interface MovieDao {
    @Upsert
    suspend fun upsertMovieList(movieList: List<MovieEntity>)

    @Query("SELECT * FROM MovieEntity WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity

    @Query("SELECT * FROM MovieEntity WHERE category = :category")
    suspend fun getMovieListByCategory (category: String): List<MovieEntity>
}