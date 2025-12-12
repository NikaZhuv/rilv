package com.example.rilv.di

import android.content.Context
import androidx.room.Room
import com.example.rilv.watchlists.local.WatchlistDao
import com.example.rilv.watchlists.local.WatchlistDatabase
import com.example.rilv.moviewatch.WatchlistMovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WatchlistDatabaseModule {

    @Provides
    @Singleton
    fun provideWatchlistDatabase(
        @ApplicationContext context: Context
    ): WatchlistDatabase =
        Room.databaseBuilder(
            context,
            WatchlistDatabase::class.java,
            "watchlists.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWatchlistDao(
        db: WatchlistDatabase
    ): WatchlistDao = db.watchlistDao

    @Provides
    @Singleton
    fun provideWatchlistMovieDao(
        db: WatchlistDatabase
    ): WatchlistMovieDao = db.watchlistMovieDao
}

