package com.example.rilv.movie.presentation

sealed interface MovieListUiEvent {
    data class Paginate(val category: String) : MovieListUiEvent
    object Navigate: MovieListUiEvent
}