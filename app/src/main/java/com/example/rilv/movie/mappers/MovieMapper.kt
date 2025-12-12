package com.example.rilv.movie.mappers

import com.example.rilv.movie.domain.model.Movie
import com.example.rilv.movie.local.movie.MovieEntity
import com.example.rilv.movie.remote.models.Result


fun Result.toMovieEntity(
    category: String
): MovieEntity {
    return MovieEntity(
        adult = adult ?: false,
        backdropPath = this.backdropPath,
        id = id ?: -1,
        originalLanguage = originalLanguage ?: "",
        overview = overview?: "",
        originalTitle = originalTitle ?: "",
        popularity = popularity ?: 0.0,
        posterPath = this.posterPath,
        releaseDate = releaseDate,
        title = title ?: "",
        video = video ?: false,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        category = category,

        genreIds = try {
            genreIds?.joinToString( ",") ?: "-1,-2"
        } catch (e: Exception) {
            "-1,-2"
        }
    )
}

fun MovieEntity.toMovie(
    category: String
): Movie {
    return Movie(
        adult = adult,
        backdropPath = this.backdropPath,
        id = id,
        originalLanguage = originalLanguage,
        overview = overview,
        originalTitle = originalTitle,
        popularity = popularity,
        posterPath = this.posterPath,
        releaseDate = releaseDate,
        title = title,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount,
        category = category,

        genreIds = try {
            genreIds.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        }
    )
}
