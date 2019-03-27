package com.movsoft.aptracker.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.movsoft.aptracker.models.TrackedItem

typealias GetTrackedItemsCompletion = (result: Result<List<TrackedItem>>) -> Unit

/**
 * Services that provide access to retrieving and persisting TrackedItems.
 */
interface TrackedItemServices {
    fun getTrackedItems(completion: GetTrackedItemsCompletion)
    fun addTrackedItem(itemName: String)
    fun deleteTrackedItem(itemName: String)
}

/**
 * Implementation of TrackedItemServices that persists TrackedItems in SharedPreferences.
 */
class SharedPreferencesTrackedItemServices(context: Context): TrackedItemServices {

    private var items: MutableList<TrackedItem>
    private var sharedPrefs: SharedPreferences

    init {
        sharedPrefs = context.getSharedPreferences("trackedItems", 0)
        items = loadItems()
    }

    override fun getTrackedItems(completion: GetTrackedItemsCompletion) {
        completion(Result.success(items))
    }

    override fun addTrackedItem(itemName: String) {
        items.add(TrackedItem(itemName))
        persist(items)
    }

    override fun deleteTrackedItem(itemName: String) {
        items.remove(TrackedItem(itemName))
        persist(items)
    }

    private fun persist(items: MutableList<TrackedItem>) {
        val json = GsonBuilder().create().toJson(items)
        val prefsEditor = sharedPrefs.edit()
        prefsEditor.putString("trackedItemsJSON", json)
        prefsEditor.apply()
    }

    private fun loadItems(): MutableList<TrackedItem> {
        val json = sharedPrefs.getString("trackedItemsJSON", "[]")
        val loadedItems = GsonBuilder().create().fromJson(json, Array<TrackedItem>::class.java)
        return loadedItems.toMutableList()
    }
}