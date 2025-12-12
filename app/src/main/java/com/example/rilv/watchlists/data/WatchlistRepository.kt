package com.example.rilv.watchlists.data

import com.example.rilv.watchlists.WatchlistEntity
import com.example.rilv.watchlists.local.WatchlistDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
private val dao: WatchlistDao
) {

    fun getAllWatchlists(): Flow<List<WatchlistEntity>> =
        dao.getAllWatchlists()

    suspend fun insert(w: WatchlistEntity): Long =
        dao.insertWatchlist(w)

    suspend fun delete(w: WatchlistEntity) =
        dao.deleteWatchlist(w)

    // Favorite
    suspend fun ensureDefaultExists() {
        val all = dao.getAllWatchlists().first()
        if (all.isEmpty()) {
            dao.insertWatchlist(
                WatchlistEntity(
                    name = "Favorite",
                    description = "Your favorite movies"
                )
            )
        }
    }

    suspend fun safeDelete(w: WatchlistEntity): Boolean {
        val all = dao.getAllWatchlists().first()

        if (all.size <= 1) {
            return false
        }

        dao.deleteWatchlist(w)
        return true
    }
}