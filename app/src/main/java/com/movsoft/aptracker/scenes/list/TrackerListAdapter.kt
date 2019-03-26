package com.movsoft.aptracker.scenes.list

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movsoft.aptracker.R

@BindingAdapter("tracked_items")
fun RecyclerView.setTrackedItems(items: List<TrackedItemViewModel>) {
    val adapter = adapter
    if (adapter is TrackerListAdapter) {
        adapter.refresh(items)
        adapter.notifyDataSetChanged()
    }
}

class TrackerListAdapter: RecyclerView.Adapter<TrackerListAdapter.ViewHolder>() {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.text_view)
        var bottomDivider: View = view.findViewById(R.id.bottom_divider)
    }

    private var items = listOf<TrackedItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.cell_tracker_item, parent,false)
        val viewHolder = ViewHolder(cell)
        viewHolder.textView = cell.findViewById(R.id.text_view)
        return ViewHolder(cell)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.itemNameText
        holder.view.setOnClickListener { item.onTap() }
        holder.bottomDivider.visibility = if (position == items.lastIndex) GONE else VISIBLE
    }

    fun refresh(trackedItems: List<TrackedItemViewModel>) {
        items = trackedItems
        notifyDataSetChanged()
    }
}