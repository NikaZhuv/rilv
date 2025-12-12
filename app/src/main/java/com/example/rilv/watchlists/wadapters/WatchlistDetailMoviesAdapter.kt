package com.example.rilv.watchlists.wadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rilv.R
import com.example.rilv.moviewatch.WatchlistMovieEntity

class WatchlistDetailMoviesAdapter(
    private val onDeleteClick: (WatchlistMovieEntity) -> Unit
) : ListAdapter<WatchlistMovieEntity, WatchlistDetailMoviesAdapter.VH>(diff) {


    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val poster: ImageView = v.findViewById(R.id.moviePoster)
        val rating: TextView = v.findViewById(R.id.movieRating)
        val title: TextView = v.findViewById(R.id.movieTitle)
        val year: TextView = v.findViewById(R.id.movieYear)
        val overview: TextView = v.findViewById(R.id.movieOverview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_vertical_watchlist, parent, false)
        return VH(v)

    }

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val item = getItem(pos)

        Glide.with(holder.itemView)
            .load(item.posterUrl)
            .into(holder.poster)

        holder.rating.text = String.format("%.1f", item.voteAverage ?: 0.0)
        holder.title.text = item.title ?: ""
        holder.year.text = item.releaseDate?.take(4) ?: ""
        holder.overview.text = item.overview ?: ""

        holder.itemView.findViewById<ImageView>(R.id.deleteMovie).setOnClickListener {
            onDeleteClick(item)
        }
    }


    companion object {
        val diff = object : DiffUtil.ItemCallback<WatchlistMovieEntity>() {
            override fun areItemsTheSame(a: WatchlistMovieEntity, b: WatchlistMovieEntity) =
                a.movieId == b.movieId

            override fun areContentsTheSame(a: WatchlistMovieEntity, b: WatchlistMovieEntity) =
                a == b
        }
    }
}
