package com.example.rilv.movie.remote.api

import com.example.rilv.BuildConfig
import com.example.rilv.movie.remote.models.MovieDto
import retrofit2.http.GET
import retrofit2.http.Query



interface MovieApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): MovieDto

//    @GET("movie/top_rated")       ///////////
//    suspend fun getPopularMovies(
//        @Query("page") page: Int,
//        @Query("api_key") apiKey: String = BuildConfig.API_KEY
//    ): MovieDto

//    @GET("movie/top_rated")
//    suspend fun getTopRatedMovies(
//        @Query("page") page: Int,
//        @Query("api_key") apiKey: String = BuildConfig.API_KEY
//    ): MovieDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): MovieDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): MovieDto

    companion object  {
        const val BASE_URL = "https://api.themoviedb.org/3/"
//        const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500/"
//        const val MOVIE_ENDPOINT ="discover/movie"
//        const val MOVIE_DETAIL_ENDPOINT ="movie"
//        const val MOVIE_ACTOR_ENDPOINT ="person"
//        const val TRENDING_MOVIE_ENDPOINT ="trending/movie/week"
//        const val MOVIE_ID ="id"
//        const val ACTOR_ID ="id"
    }
}
