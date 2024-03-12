package com.movsoft.aptracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movsoft.aptracker.scenes.add_item.AddTrackedItemViewModelImpl
import com.movsoft.aptracker.scenes.add_item.EditTrackedItemViewModel
import com.movsoft.aptracker.scenes.focus.FocusViewModel
import com.movsoft.aptracker.scenes.list.TrackerListViewModel
import com.movsoft.aptracker.scenes.settings.SettingsViewModel
import com.movsoft.aptracker.services.*

class APTrackerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        DependencyProvider.configure(this)
    }

    /**
     * Singleton for managing service and view model dependencies.
     */
    object DependencyProvider: ViewModelProvider.Factory {

        val viewModelFactory get() = this

        private lateinit var application: Application
        private lateinit var settingsServices: SettingsServices
        private lateinit var trackedItemServices: TrackedItemServices
        private lateinit var trackingServices: TrackingServices

        fun configure(context: Application) {
            application = context
            settingsServices = SharedPreferencesSettingsServices(context)
            trackingServices = RafiTrackingServices(context, settingsServices)
            trackedItemServices = SharedPreferencesTrackedItemServices(context)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SettingsViewModel::class.java -> SettingsViewModel(settingsServices) as T
                TrackerListViewModel::class.java -> TrackerListViewModel(trackingServices, trackedItemServices, settingsServices) as T
                FocusViewModel::class.java -> FocusViewModel(trackedItemServices, trackingServices) as T
                AddTrackedItemViewModelImpl::class.java -> AddTrackedItemViewModelImpl(application, trackedItemServices) as T
                EditTrackedItemViewModel::class.java -> EditTrackedItemViewModel(application, trackedItemServices) as T
                else -> modelClass.getDeclaredConstructor().newInstance()
            }
        }
    }
}

