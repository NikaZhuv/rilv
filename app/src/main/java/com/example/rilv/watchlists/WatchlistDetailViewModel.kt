package com.example.rilv.watchlists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.rilv.watchlists.data.WatchlistRepository
import com.example.rilv.moviewatch.WatchlistMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistDetailViewModel @Inject constructor(
    private val repo: WatchlistRepository,
    private val movieRepo: WatchlistMovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val watchlistId: Long = savedStateHandle["watchlistId"] ?: -1

    // список фильмов текущего вотчлиста
    val movies = movieRepo.getMovies(watchlistId).asLiveData()

    fun deleteWatchlist(
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        viewModelScope.launch {
            val all = repo.getAllWatchlists().first()
            val wl = all.firstOrNull { it.id == watchlistId }

            if (wl == null) {
                onFailed()
                return@launch
            }

            val ok = repo.safeDelete(wl)

            if (ok) onSuccess()
            else onFailed()
        }
    }

    fun removeMovie(watchlistId: Long, movieId: Long) {
        viewModelScope.launch {
            movieRepo.removeMovie(watchlistId, movieId)
        }
    }

}
