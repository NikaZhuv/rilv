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

class VerticalMovieAdapter(
    private var movies: List<Movie>,
    private val onClick: (Movie) -> Unit,
    private val onWatchlistClick: (Movie) -> Unit
) : RecyclerView.Adapter<VerticalMovieAdapter.MovieViewHolder>() {


    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.moviePoster)
        val title: TextView = view.findViewById(R.id.movieTitle)
        val year: TextView = view.findViewById(R.id.movieYear)
        val overview: TextView = view.findViewById(R.id.movieOverview)

        val rating: TextView = view.findViewById(R.id.movieRating)
        val watchlistBtn: ImageView = view.findViewById(R.id.addWatchlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_vertical, parent, false)
        return MovieViewHolder(view)


    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.title.text = movie.title ?: ""
        holder.year.text = movie.releaseDate?.take(4) ?: ""
        holder.overview.text = movie.overview ?: ""


        val posterUrl = "https://image.tmdb.org/t/p/w500" + movie.posterPath

        Glide.with(holder.poster)
            .load(posterUrl)
            .placeholder(R.drawable.rounded_imagesmode_24)
            .into(holder.poster)

        holder.itemView.setOnClickListener {
            onClick(movie)
        }

        holder.rating.text = String.format("%.1f", movie.voteAverage ?: 0.0)

        holder.watchlistBtn.setOnClickListener {
            onWatchlistClick(movie)
        }

        Log.d("DEBUG_POSTER", "posterPath = ${movie.posterPath}")

    }

    fun submitList(newList: List<Movie>) {
        movies = newList
        notifyDataSetChanged()
    }
}
