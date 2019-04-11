package com.movsoft.aptracker.scenes.list

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.models.TrackItemResult
import com.movsoft.aptracker.models.TrackItemResult.Status.STARTED
import com.movsoft.aptracker.scenes.base.ViewModelState
import com.movsoft.aptracker.scenes.base.ViewModelState.*
import com.movsoft.aptracker.scenes.base.ViewModelStatePlaceholder.NoContent
import com.movsoft.aptracker.scenes.base.ViewModelStatePlaceholder.SettingsNotValid
import com.movsoft.aptracker.services.SettingsServices
import com.movsoft.aptracker.services.TrackedItemServices
import com.movsoft.aptracker.services.TrackingServices

class TrackerListViewModel(
    private val trackingServices: TrackingServices,
    private val trackedItemServices: TrackedItemServices,
    private val settingsServices: SettingsServices
): ViewModel(), Observable {

    interface Listener {
        fun showMessage(message: String)
        fun showError(message: String)
    }

    var listener: Listener? = null

    var state: MutableLiveData<ViewModelState> = MutableLiveData()

    var trackedItems: MutableLiveData<List<TrackedItemViewModel>> = MutableLiveData()

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    fun refresh() {
        //Show a placeholder screen if settings aren't set up
        if (!settingsServices.getSettings().isValid()) {
            state.value = Placeholder(SettingsNotValid)
            return
        }

        //Fetch list of tracked items
        state.value = Loading
        trackedItemServices.getTrackedItems { result ->
            val fetchedItems = result.getOrDefault(listOf())
            trackedItems.value = fetchedItems.map { TrackedItemViewModel(it) }
            state.value = if (fetchedItems.isEmpty()) Placeholder(NoContent) else Complete
        }
    }

    fun addItem(itemName: String) {
        trackedItemServices.addTrackedItem(itemName)
        refresh()
    }

    fun deleteItem(itemIdentifier: String) {
        trackedItemServices.deleteTrackedItem(itemIdentifier)
        refresh()
    }

    fun trackItem(itemIdentifier: String) {
        val item = trackedItems.value?.first { it.itemIdentifier == itemIdentifier }
        trackingServices.track(itemIdentifier) { result ->
            if (result.isFailure) listener?.showError("Error tracking ${item?.itemNameText}")
            else {
                val trackingResult = result.getOrDefault(TrackItemResult(STARTED))
                val startStop = if (trackingResult.status == STARTED) "Started" else "Stopped"
                listener?.showMessage("$startStop tracking ${item?.itemNameText}")
            }
        }
    }

    fun trackAverageItem(itemIdentifier: String) {
        val item = trackedItems.value?.first { it.itemIdentifier == itemIdentifier }
        trackingServices.trackAverage(itemIdentifier) { result ->
            if (result.isFailure) listener?.showError("Error tracking ${item?.itemNameText}")
            else {
                listener?.showMessage("Tracked average ${item?.itemNameText}")
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Observable Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }
}
