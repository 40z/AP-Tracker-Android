package com.movsoft.aptracker.scenes.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.movsoft.aptracker.models.TrackedItem
import com.movsoft.aptracker.services.TrackedItemProvider
import com.movsoft.aptracker.services.TrackedItemServices
import com.movsoft.aptracker.services.TrackingServices

class TrackerListViewModel(val trackedItemProvider: TrackedItemProvider, val trackingServices: TrackingServices, val trackedItemServices: TrackedItemServices, val listener: Listener): ViewModel(), TrackerItemViewModel.Listener {

    interface Listener {
        fun showMessage(message: String)
        fun showError(message: String)
    }

    val trackedItems: MutableLiveData<List<TrackerItemViewModel>> = MutableLiveData()

    fun refresh() {
        trackedItems.value = trackedItemProvider.getTrackedItems().map { TrackerItemViewModel(it, this) }
    }

    fun addItem(item: String) {
        trackedItemServices.addTrackedItem(item)
        refresh()
    }

    override fun onTrackedItemSelected(item: TrackerItemViewModel) {
        trackingServices.track(item.itemNameText, Response.Listener<String> { stringVal ->
            listener.showMessage("Started tracking ${item.itemNameText}")
        }, Response.ErrorListener { error ->
            listener.showError("Error tracking ${item.itemNameText}")
        })
    }
}

class TrackerItemViewModel(trackedItem: TrackedItem, val listener: Listener) {

    interface Listener {
        fun onTrackedItemSelected(item: TrackerItemViewModel)
    }

    val itemNameText: String

    init {
        itemNameText = trackedItem.name
    }

    fun onTap() {
        listener.onTrackedItemSelected(this)
    }
}