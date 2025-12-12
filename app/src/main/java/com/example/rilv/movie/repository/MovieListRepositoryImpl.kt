package com.example.rilv.movie.repository

import com.example.rilv.movie.domain.model.Movie
import com.example.rilv.movie.domain.repository.MovieListRepository
import com.example.rilv.movie.local.movie.MovieDatabase
import com.example.rilv.movie.mappers.toMovie
import com.example.rilv.movie.mappers.toMovieEntity
import com.example.rilv.movie.remote.api.MovieApiService
import com.example.rilv.utils.Category
import com.example.rilv.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MovieListRepositoryImpl @Inject constructor(
    private val api: MovieApiService,
    private val db: MovieDatabase
) : MovieListRepository {

    override suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> = flow {

        emit(Resource.Loading(true))

        val dao = db.movieDao
        val local = dao.getMovieListByCategory(category)


        if (local.isNotEmpty() && !forceFetchFromRemote) {
            emit(Resource.Success(local.map { it.toMovie(category) }))
            emit(Resource.Loading(false))
            return@flow
        }

        val remote = when (category) {
            Category.POPULAR -> api.getPopularMovies(page)
            Category.UPCOMING -> api.getUpcomingMovies(page)
            Category.NEW -> api.getNowPlayingMovies(page)
            else -> null
        }

        val movies = remote?.results?.map { result ->
            val entity = result.toMovieEntity(category)
            entity
        } ?: emptyList()


        if (movies.isNotEmpty())
            dao.upsertMovieList(movies)

        emit(Resource.Success(movies.map { it.toMovie(category) }))
        emit(Resource.Loading(false))
    }

    override suspend fun getMovie(id: Int): Flow<Resource<Movie>> = flow {
        emit(Resource.Loading(true))

        val movie = db.movieDao.getMovieById(id)

        if (movie != null) {
            emit(Resource.Success(movie.toMovie(movie.category)))
        } else {
            emit(Resource.Error("Movie not found"))
        }

        emit(Resource.Loading(false))
    }
}
