package com.movsoft.aptracker.scenes.settings

import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.models.AppSettings
import com.movsoft.aptracker.services.SettingsServices

/**
 * ViewModel for settings screen.
 */
class SettingsViewModel(private val settingsServices: SettingsServices): ViewModel() {

    var userNameText: String? = null
    var trackingChannelText: String? = null
    var rafiHostText: String? = null

    fun refresh() {
        val savedSettings = settingsServices.getSettings()
        userNameText = savedSettings.userName
        trackingChannelText = savedSettings.trackingChannel
        rafiHostText = savedSettings.server
    }

    fun save() {
        val settingsFromForm = AppSettings(userNameText, rafiHostText, trackingChannelText)
        settingsServices.saveSettings(settingsFromForm)
    }
}