package com.example.rilv.moviewatch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistMovieDao {

    @Insert
    suspend fun insert(entity: WatchlistMovieEntity)

    @Query("DELETE FROM watchlist_movies WHERE watchlistId = :watchlistId AND movieId = :movieId")
    suspend fun delete(watchlistId: Long, movieId: Long)

    @Query("SELECT * FROM watchlist_movies")
    fun getAllMovies(): Flow<List<WatchlistMovieEntity>>

    @Query("SELECT * FROM watchlist_movies WHERE watchlistId = :watchlistId")
    fun getMoviesForWatchlist(watchlistId: Long): Flow<List<WatchlistMovieEntity>>

    @Query("""
        SELECT posterUrl FROM watchlist_movies 
        WHERE watchlistId = :watchlistId 
        LIMIT 4     
    """)
    suspend fun getPreviewPosters(watchlistId: Long): List<String?>
}