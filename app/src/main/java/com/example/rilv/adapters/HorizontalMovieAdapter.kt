package com.example.rilv.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rilv.R
import com.example.rilv.movie.domain.model.Movie

class HorizontalMovieAdapter(
    private var movies: List<Movie>,
    private val onClick: (Movie) -> Unit,
    private val onWatchlistClick: (Movie) -> Unit
) : RecyclerView.Adapter<HorizontalMovieAdapter.MovieViewHolder>() {


    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.imagePoster)
        val title: TextView = view.findViewById(R.id.textTitle)

        val rating: TextView = view.findViewById(R.id.movieRating)
        val watchlistBtn: ImageView = view.findViewById(R.id.addWatchlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_horizontal, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.title.text = movie.title ?: ""

        Glide.with(holder.poster)
            .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
            .into(holder.poster)

        // РЕЙТИНГ
        holder.rating.text = String.format("%.1f", movie.voteAverage ?: 0.0)

        // + В WATCHLIST
        holder.watchlistBtn.setOnClickListener {
            onWatchlistClick(movie)
        }

        holder.itemView.setOnClickListener {
            onClick(movie)
        }

        Log.d("DEBUG_POSTER", "posterPath = ${movie.posterPath}")
    }

    fun submitList(newList: List<Movie>) {
        movies = newList
        notifyDataSetChanged()
    }
}

