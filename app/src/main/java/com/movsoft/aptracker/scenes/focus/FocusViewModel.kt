package com.movsoft.aptracker.scenes.focus

import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.models.TrackItemResult
import com.movsoft.aptracker.models.TrackItemResult.Status.STARTED
import com.movsoft.aptracker.models.TrackedItem
import com.movsoft.aptracker.services.TrackedItemServices
import com.movsoft.aptracker.services.TrackingServices

class FocusViewModel(private val trackedItemServices: TrackedItemServices, private val trackingServices: TrackingServices): ViewModel() {

    interface Listener {
        fun showMessage(message: String)
        fun showError(message: String)
    }

    var itemNameText: String? = null
        private set

    var listener: Listener? =  null

    private var item: TrackedItem? = null

    fun refresh(itemIdentifier: String) {
        item = trackedItemServices.getTrackedItem(itemIdentifier)
        itemNameText = item?.name
    }

    fun trackItem() {
        val frozenItem = item ?: return
        trackingServices.track(frozenItem.name) { result ->
            if (result.isFailure) listener?.showError("Error tracking $itemNameText")
            else {
                val trackingResult = result.getOrDefault(TrackItemResult(STARTED))
                val startStop = if (trackingResult.status == STARTED) "Started" else "Stopped"
                listener?.showMessage("$startStop tracking $itemNameText")
            }
        }
    }
}