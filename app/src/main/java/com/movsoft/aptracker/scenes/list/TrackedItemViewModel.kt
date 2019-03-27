package com.movsoft.aptracker.scenes.list

import com.movsoft.aptracker.models.TrackedItem

class TrackedItemViewModel(trackedItem: TrackedItem, val listener: Listener) {

    interface Listener {
        fun onTrackedItemSelected(item: TrackedItemViewModel)
        fun onTrackedItemDeleted(item: TrackedItemViewModel)
    }

    val itemNameText: String

    init {
        itemNameText = trackedItem.name
    }

    fun onTap() {
        listener.onTrackedItemSelected(this)
    }

    fun onDelete() {
        listener.onTrackedItemDeleted(this)
    }
}
