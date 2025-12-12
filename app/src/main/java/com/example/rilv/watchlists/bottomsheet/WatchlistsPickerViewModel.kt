package com.example.rilv.watchlists.bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.rilv.watchlists.data.WatchlistRepository
import com.example.rilv.moviewatch.WatchlistMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistsPickerViewModel @Inject constructor(
    private val repo: WatchlistRepository,
    private val moviesRepo: WatchlistMovieRepository
) : ViewModel() {

    val watchlists = repo.getAllWatchlists().asLiveData()

    fun toggleMovie(
        watchlistId: Long,
        movieId: Long,
        poster: String?,
        title: String?,
        overview: String?,
        releaseDate: String?,
        rating: Double?
    ) {
        viewModelScope.launch {
            val current = moviesRepo.getMovies(watchlistId).first()
            val exists = current.any { it.movieId == movieId }

            if (exists) {
                moviesRepo.removeMovie(watchlistId, movieId)
            } else {
                moviesRepo.addMovie(
                    watchlistId = watchlistId,
                    movieId = movieId,
                    poster = poster,
                    title = title,
                    overview = overview,
                    releaseDate = releaseDate,
                    rating = rating
                )
            }
        }
    }


    fun getSelectedWatchlists(movieId: Long) =
        moviesRepo.getMoviesFlowForAllWatchlists()
            .map { map ->
                map.filter { (_, movies) ->
                    movies.any { it.movieId == movieId }
                }.keys.toSet()
            }
            .asLiveData()
}
