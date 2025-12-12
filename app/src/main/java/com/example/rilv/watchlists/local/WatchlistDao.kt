package com.example.rilv.watchlists.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.rilv.watchlists.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlists ORDER BY id DESC")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Insert
    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)
}
