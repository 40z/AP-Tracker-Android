package com.movsoft.aptracker.scenes.list

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.BuildConfig
import com.movsoft.aptracker.models.TrackItemResult
import com.movsoft.aptracker.models.TrackItemResult.Status.*
import com.movsoft.aptracker.models.TrackedItem
import com.movsoft.aptracker.models.TrackedItemSettings
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

    var shouldShowWhatsNew: MutableLiveData<Boolean> = MutableLiveData()

    var lastUsedVersionCode: Int = 0

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    fun refresh() {

        //Show whats new if they haven't seen release notes for this version
        lastUsedVersionCode = settingsServices.getLastUsedVersionCode()
        shouldShowWhatsNew.value = lastUsedVersionCode < BuildConfig.VERSION_CODE

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
        val newItem = TrackedItem(name = itemName)
        trackedItemServices.addTrackedItem(newItem)
        refresh()
    }

    fun editItem(itemIdentifier: String, itemName: String) {
        val item = trackedItemServices.getTrackedItem(itemIdentifier) ?: return
        item.name = itemName
        trackedItemServices.updateTrackedItem(item)
        refresh()
    }

    fun deleteItem(itemIdentifier: String) {
        trackedItemServices.deleteTrackedItem(itemIdentifier)
        refresh()
    }

    fun trackItem(itemIdentifier: String) {
        val item = trackedItemServices.getTrackedItem(itemIdentifier) ?: return
        trackingServices.track(item) { result ->
            if (result.isFailure) listener?.showError("Error tracking ${item.name}")
            else {
                val trackingResult = result.getOrDefault(TrackItemResult(STARTED))
                when(trackingResult.status) {
                    STARTED -> listener?.showMessage("Started tracking ${item.name}")
                    STOPPED -> listener?.showMessage("Stopped tracking ${item.name}")
                    SINGLE -> listener?.showMessage("Tracked single ${item.name}")
                }
            }
        }
    }

    fun trackAverageItem(itemIdentifier: String) {
        val item = trackedItemServices.getTrackedItem(itemIdentifier) ?: return
        trackingServices.trackAverage(item) { result ->
            if (result.isFailure) listener?.showError("Error tracking ${item.name}")
            else {
                listener?.showMessage("Tracked average ${item.name}")
            }
        }
    }

    fun markWhatsNewAsSeen() {
        lastUsedVersionCode = BuildConfig.VERSION_CODE
        settingsServices.saveLastUsedVersionCode(lastUsedVersionCode)
        shouldShowWhatsNew.value = false
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
