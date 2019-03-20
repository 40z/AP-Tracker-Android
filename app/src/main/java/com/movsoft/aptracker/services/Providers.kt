package com.movsoft.aptracker.services

import com.movsoft.aptracker.models.TrackedItem

interface TrackedItemProvider {
    fun getTrackedItems(): List<TrackedItem>
}

interface TrackedItemServices {
    fun getTrackedItems(): List<TrackedItem>
    fun addTrackedItem(itemName: String)
}

class SharedPreferencesTrackedItemProvider: TrackedItemProvider, TrackedItemServices {
    val items = arrayListOf<TrackedItem>()

    override fun getTrackedItems(): List<TrackedItem> {
        return items
    }

    override fun addTrackedItem(itemName: String) {
        items.add(TrackedItem(itemName))
    }
}