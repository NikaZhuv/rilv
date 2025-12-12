package com.example.rilv.moviewatch

import androidx.room.Entity

@Entity(
    tableName = "watchlist_movies",
    primaryKeys = ["watchlistId", "movieId"]
)
data class WatchlistMovieEntity(
    val watchlistId: Long,
    val movieId: Long,

    val posterUrl: String?,

    val title: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)