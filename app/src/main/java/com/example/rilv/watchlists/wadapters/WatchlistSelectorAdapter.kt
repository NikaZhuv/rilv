package com.example.rilv.watchlists.wadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rilv.R
import com.example.rilv.watchlists.WatchlistEntity
import android.widget.ImageView

class WatchlistSelectorAdapter(
    private var lists: List<WatchlistEntity> = emptyList(),
    private var selected: Set<Long> = emptySet(),
    private val onClick: (WatchlistEntity) -> Unit
) : RecyclerView.Adapter<WatchlistSelectorAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.watchlistItem)
        val check: ImageView = v.findViewById(R.id.checkmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist_selector, parent, false)
        return VH(v)
    }

    override fun getItemCount() = lists.size

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val item = lists[pos]

        holder.title.text = item.name

        val isSelected = selected.contains(item.id)

        holder.check.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        holder.itemView.alpha = if (isSelected) 1f else 0.6f

        holder.itemView.setOnClickListener { onClick(item) }
    }

    fun submitLists(newList: List<WatchlistEntity>) {
        lists = newList
        notifyDataSetChanged()
    }

    fun setSelected(ids: Set<Long>) {
        selected = ids
        notifyDataSetChanged()
    }
}
