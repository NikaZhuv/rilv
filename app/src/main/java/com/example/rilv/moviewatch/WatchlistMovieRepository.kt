package com.example.rilv.moviewatch

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
class WatchlistMovieRepository @Inject constructor(
    private val dao: WatchlistMovieDao
) {

    fun getMoviesFlowForAllWatchlists(): Flow<Map<Long, List<WatchlistMovieEntity>>> =
        dao.getAllMovies()
            .map { movies ->
                movies.groupBy { it.watchlistId }
            }

    suspend fun addMovie(
        watchlistId: Long,
        movieId: Long,
        poster: String?,
        title: String?,
        overview: String?,
        releaseDate: String?,
        rating: Double?
    ) {
        dao.insert(
            WatchlistMovieEntity(
                watchlistId = watchlistId,
                movieId = movieId,
                posterUrl = poster,
                title = title,
                overview = overview,
                releaseDate = releaseDate,
                voteAverage = rating
            )
        )
    }


    suspend fun removeMovie(watchlistId: Long, movieId: Long) {
        dao.delete(watchlistId, movieId)
    }

    fun getMovies(watchlistId: Long): Flow<List<WatchlistMovieEntity>> =
        dao.getMoviesForWatchlist(watchlistId)

    suspend fun getPreviewPosters(watchlistId: Long): List<String?> =
        dao.getPreviewPosters(watchlistId)
}
