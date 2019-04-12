package com.movsoft.aptracker.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.movsoft.aptracker.models.TrackedItem
import java.lang.reflect.Type

typealias GetTrackedItemsCompletion = (result: Result<List<TrackedItem>>) -> Unit

/**
 * Services that provide access to retrieving and persisting TrackedItems.
 */
interface TrackedItemServices {
    fun getTrackedItems(completion: GetTrackedItemsCompletion)
    fun getTrackedItem(itemIdentifier: String): TrackedItem?
    fun addTrackedItem(itemName: String)
    fun updateTrackedItem(item: TrackedItem)
    fun deleteTrackedItem(itemIdentifier: String)
}

/**
 * Implementation of TrackedItemServices that persists TrackedItems in SharedPreferences.
 */
class SharedPreferencesTrackedItemServices(context: Context): TrackedItemServices {

    private var items: LinkedHashSet<TrackedItem>
    private var sharedPrefs: SharedPreferences

    init {
        sharedPrefs = context.getSharedPreferences("trackedItems", 0)
        items = loadItems()
    }

    override fun getTrackedItems(completion: GetTrackedItemsCompletion) {
        completion(Result.success(items.toList()))
    }

    override fun getTrackedItem(itemIdentifier: String): TrackedItem? {
        return items.firstOrNull { it.identifier == itemIdentifier }
    }

    override fun addTrackedItem(itemName: String) {
        val newItem = TrackedItem(name = itemName)
        items.add(newItem)
        persist(items)
    }

    override fun updateTrackedItem(item: TrackedItem) {
        items.add(item)
        persist(items)
    }

    override fun deleteTrackedItem(itemIdentifier: String) {
        val item = items.first { it.identifier == itemIdentifier }
        items.remove(item)
        persist(items)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Helpers
    //------------------------------------------------------------------------------------------------------------------

    private fun persist(items: LinkedHashSet<TrackedItem>) {
        val json = GsonBuilder().create().toJson(items)
        val prefsEditor = sharedPrefs.edit()
        prefsEditor.putString("trackedItemsJSON", json)
        prefsEditor.apply()
    }

    private fun loadItems(): LinkedHashSet<TrackedItem> {
        val json = sharedPrefs.getString("trackedItemsJSON", "[]")
        val builder = GsonBuilder()
        builder.registerTypeAdapter(TrackedItem::class.java, TrackedItem.Deserializer())
        val type: Type = object : TypeToken<LinkedHashSet<TrackedItem>>() {}.type
        return builder.create().fromJson(json, type)
    }
}
