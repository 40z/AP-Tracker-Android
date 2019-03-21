package com.movsoft.aptracker.scenes.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.services.TrackedItemServices
import com.movsoft.aptracker.services.TrackingServices

class TrackerListViewModel(val trackingServices: TrackingServices, val trackedItemServices: TrackedItemServices): ViewModel(), TrackedItemViewModel.Listener {

    interface Listener {
        fun showMessage(message: String)
        fun showError(message: String)
    }

    var listener: Listener? = null
    val trackedItems: MutableLiveData<List<TrackedItemViewModel>> = MutableLiveData()

    fun refresh() {
        trackedItemServices.getTrackedItems { result ->
            val fetchedItems = result.getOrDefault(listOf())
            trackedItems.value = fetchedItems.map { TrackedItemViewModel(it, this) }
        }
    }

    fun addItem(item: String) {
        trackedItemServices.addTrackedItem(item)
        refresh()
    }

    override fun onTrackedItemSelected(item: TrackedItemViewModel) {
        trackingServices.track(item.itemNameText) { result ->
            if (result.isSuccess) listener?.showMessage("Started tracking ${item.itemNameText}")
            else listener?.showError("Error tracking ${item.itemNameText}")
        }
    }
}
