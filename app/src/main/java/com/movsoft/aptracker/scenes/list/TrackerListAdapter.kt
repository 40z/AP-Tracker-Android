package com.movsoft.aptracker.scenes.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.CellTrackerItemBinding

class TrackerListAdapter(private val handler: TrackerListActionHandler): RecyclerView.Adapter<TrackerListAdapter.ViewHolder>() {

    class ViewHolder(val binding: CellTrackerItemBinding): RecyclerView.ViewHolder(binding.root)

    private var items = listOf<TrackedItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cellBinding = DataBindingUtil.inflate<CellTrackerItemBinding>(inflater, R.layout.cell_tracker_item, parent, false)
        return ViewHolder(cellBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.handler = handler
        holder.binding.viewModel = item
        holder.binding.swipeReveal.close(false)
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