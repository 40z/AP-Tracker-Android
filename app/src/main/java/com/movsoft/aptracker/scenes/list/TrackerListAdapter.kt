package com.movsoft.aptracker.scenes.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.movsoft.aptracker.R

@BindingAdapter("tracked_items")
fun RecyclerView.setTrackedItems(items: LiveData<List<TrackedItemViewModel>>) {
    val adapter = adapter
    if (adapter is TrackerListAdapter) {
        adapter.refresh(items.value ?: listOf())
    }
}

class TrackerListAdapter: RecyclerView.Adapter<TrackerListAdapter.ViewHolder>() {

    class ViewHolder(val view: SwipeRevealLayout): RecyclerView.ViewHolder(view) {
        var cell: View = view.findViewById(R.id.cell)
        var textView: TextView = view.findViewById(R.id.text_view)
        var deleteButton: View = view.findViewById(R.id.delete_button)
    }

    private var items = listOf<TrackedItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.cell_tracker_item, parent,false)
        val viewHolder = ViewHolder(cell as SwipeRevealLayout)
        viewHolder.textView = cell.findViewById(R.id.text_view)
        return ViewHolder(cell)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.view.close(false)
        holder.textView.text = item.itemNameText
        holder.cell.setOnClickListener { item.onTap() }
        holder.deleteButton.setOnClickListener { item.onDelete() }
    }

    fun refresh(trackedItems: List<TrackedItemViewModel>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(items, trackedItems))
        diffResult.dispatchUpdatesTo(this)
        items = trackedItems
    }

    //------------------------------------------------------------------------------------------------------------------
    // DiffUtil
    //------------------------------------------------------------------------------------------------------------------

    private class ItemDiffCallback(
        val originalList: List<TrackedItemViewModel>,
        val newList: List<TrackedItemViewModel>
    ): DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return originalList[oldItemPosition].itemNameText == newList[newItemPosition].itemNameText
        }

        override fun getOldListSize(): Int {
            return originalList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return originalList[oldItemPosition].itemNameText == newList[newItemPosition].itemNameText
        }
    }
}