package com.example.rilv.watchlists.wadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rilv.R
import com.example.rilv.watchlists.WatchlistEntity
import com.example.rilv.moviewatch.WatchlistMovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WatchlistsAdapter(
    private var lists: List<WatchlistEntity> = emptyList(),
    private val movieRepo: WatchlistMovieRepository,
    private val scope: CoroutineScope,
    private val onClick: (WatchlistEntity) -> Unit
) : RecyclerView.Adapter<WatchlistsAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.watchlistName)
        val desc: TextView = v.findViewById(R.id.watchlistDescription)

        val posters = listOf<ImageView>(
            v.findViewById(R.id.poster1),
            v.findViewById(R.id.poster2),
            v.findViewById(R.id.poster3),
            v.findViewById(R.id.poster4),
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist, parent, false)
        return VH(v)
    }

    override fun getItemCount() = lists.size

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val wl = lists[pos]

        holder.name.text = wl.name
        holder.desc.text = wl.description

        holder.itemView.setOnClickListener { onClick(wl) }

        holder.posters.forEach { img ->
            img.setImageDrawable(null)
            img.setBackgroundResource(R.drawable.rounded_imagesmode_24)
        }

        // Асинхронная загрузка 4 постеров
        scope.launch {
            val posters = movieRepo.getPreviewPosters(wl.id)

            posters.forEachIndexed { i, relativeUrl ->
                if (i < holder.posters.size && !relativeUrl.isNullOrEmpty()) {

                    //
                    val fullUrl = "https://image.tmdb.org/t/p/w500$relativeUrl"

                    Glide.with(holder.itemView)
                        .load(fullUrl)
                        .placeholder(R.drawable.rounded_imagesmode_24)
                        .into(holder.posters[i])
                }
            }
        }
    }

    fun submit(list: List<WatchlistEntity>) {
        lists = list
        notifyDataSetChanged()
    }
}

