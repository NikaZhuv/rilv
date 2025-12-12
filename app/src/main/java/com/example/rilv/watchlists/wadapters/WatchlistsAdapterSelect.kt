package com.example.rilv.watchlists.wadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rilv.R
import com.example.rilv.watchlists.WatchlistEntity

class WatchlistsAdapterSelect(
    private val onClick: (WatchlistEntity) -> Unit
) : ListAdapter<WatchlistEntity, WatchlistsAdapterSelect.VH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist_selector, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.text.text = item.name
        holder.itemView.setOnClickListener { onClick(item) }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.watchlistItem)
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<WatchlistEntity>() {
            override fun areItemsTheSame(a: WatchlistEntity, b: WatchlistEntity) = a.id == b.id
            override fun areContentsTheSame(a: WatchlistEntity, b: WatchlistEntity) = a == b
        }
    }
}
