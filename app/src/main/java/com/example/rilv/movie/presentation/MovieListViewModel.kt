package com.example.rilv.movie.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rilv.movie.domain.repository.MovieListRepository
import com.example.rilv.utils.Category
import com.example.rilv.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieListRepository: MovieListRepository
): ViewModel() {
    private var _movieListState = MutableStateFlow(MovieListState())
    val movieListState = _movieListState.asStateFlow()

    private val maxPopularPages = 5

    init {
        getPopularMovieList(false)
        getUpcomingMovieList(false)
        getNowPlayingMovieList(false)
    }

    fun onEvent(event: MovieListUiEvent) {
        when(event) {
            MovieListUiEvent.Navigate -> {
                _movieListState.update {
                    it.copy(
                        isCurrentPopularScreen = !movieListState.value.isCurrentPopularScreen
                    )
                }
            }
            is MovieListUiEvent.Paginate -> {
                if (event.category == Category.POPULAR) {
                    getPopularMovieList(true)
                } else if (event.category == Category.UPCOMING){
                    getUpcomingMovieList(true)
                } else if (event.category == Category.NEW){
                    getNowPlayingMovieList(true)
                }
            }
        }
    }

    private fun getPopularMovieList(forceFetchFromRemote: Boolean) {

        if (_movieListState.value.isPaginating) return

        if (_movieListState.value.popularMovieListPage > maxPopularPages) return

        _movieListState.update { it.copy(isPaginating = true) }


        viewModelScope.launch {
            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.POPULAR,
                movieListState.value.popularMovieListPage
            ).collectLatest { result ->
                when(result) {
                    is Resource.Success -> {
                        _movieListState.update {
                            it.copy(
                                popularMovieList = (it.popularMovieList + result.data!!)
                                    .distinctBy { m -> m.id },
//                                  .shuffled(),
                                popularMovieListPage = it.popularMovieListPage + 1,
                                isPaginating = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _movieListState.update { it.copy(isPaginating = false) }
                    }
                    is Resource.Loading -> Unit
                }
            }
        }

    }
    private fun getUpcomingMovieList(forceFetchFromRemote: Boolean) {
        viewModelScope.launch {
            _movieListState.update {
                it.copy(isLoading = true)
            }

            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.UPCOMING,
                movieListState.value.upcomingMovieListPage
            ).collectLatest { result ->
                when(result) {
                    is Resource.Error<*> -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Loading<*> -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                    is Resource.Success<*> -> {
                        result.data?.let { upcomingList ->
                            _movieListState.update {
                                it.copy(
                                    upcomingMovieList = movieListState.value.upcomingMovieList
                                            + upcomingList,
                                    upcomingMovieListPage = movieListState.value.upcomingMovieListPage + 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getNowPlayingMovieList(forceFetchFromRemote: Boolean) {
        viewModelScope.launch {
            _movieListState.update {
                it.copy(isLoading = true)
            }

            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.NEW,
                movieListState.value.nowPlayingMovieListPage
            ).collectLatest { result ->
                when(result) {
                    is Resource.Error<*> -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Loading<*> -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                    is Resource.Success<*> -> {
                        result.data?.let { nowPlayingList ->
                            _movieListState.update {
                                it.copy(
                                    nowPlayingMovieList = movieListState.value.nowPlayingMovieList
                                            + nowPlayingList,
                                    nowPlayingMovieListPage = movieListState.value.nowPlayingMovieListPage + 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}