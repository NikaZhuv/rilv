package com.example.rilv.movie.presentation

import com.example.rilv.movie.domain.model.Movie

data class MovieListState (
    val isLoading: Boolean = false,

    val popularMovieListPage: Int = 1,
    val upcomingMovieListPage: Int = 1,
    val nowPlayingMovieListPage: Int = 1,

    val isCurrentPopularScreen: Boolean = true,
    val isPaginating: Boolean = false,

    val popularMovieList: List<Movie> = emptyList(),
    val upcomingMovieList: List<Movie> = emptyList(),
    val nowPlayingMovieList: List<Movie> = emptyList()
)