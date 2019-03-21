package com.movsoft.aptracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movsoft.aptracker.scenes.list.TrackerListViewModel
import com.movsoft.aptracker.scenes.settings.SettingsViewModel
import com.movsoft.aptracker.services.*

class APTrackerApplication: Application(), ViewModelProvider.Factory {

    val viewModelFactory get() = this

    private lateinit var settingsServices: SettingsServices
    private lateinit var trackedItemServices: TrackedItemServices
    private lateinit var trackingServices: TrackingServices

    override fun onCreate() {
        super.onCreate()
        settingsServices = SharedPreferencesSettingsServices(this)
        trackingServices = RafiTrackingServices(this, settingsServices)
        trackedItemServices = SharedPreferencesTrackedItemServices(this)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SettingsViewModel::class.java -> SettingsViewModel(settingsServices) as T
            TrackerListViewModel::class.java -> TrackerListViewModel(trackingServices, trackedItemServices) as T
            else -> modelClass.newInstance()
        }
    }
}