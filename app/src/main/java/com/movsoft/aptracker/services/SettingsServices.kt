package com.movsoft.aptracker.services

import android.content.Context
import com.google.gson.GsonBuilder
import com.movsoft.aptracker.models.Settings

/**
 * Services that retrieve and persist Settings.
 */
interface SettingsServices {
    fun saveSettings(settings: Settings)
    fun getSettings(): Settings
}

/**
 * Implementation of SettingsServices that persists Settings in SharedPreferences.
 */
class SharedPreferencesSettingsServices(context: Context): SettingsServices {

    private var sharedPrefs = context.getSharedPreferences("settings", 0)

    override fun saveSettings(settings: Settings) {
        val json = GsonBuilder().create().toJson(settings)
        val prefsEditor = sharedPrefs.edit()
        prefsEditor.putString("savedSettings", json)
        prefsEditor.apply()
    }

    override fun getSettings(): Settings {
        val json = sharedPrefs.getString("savedSettings", "{}")
        return GsonBuilder().create().fromJson(json, Settings::class.java)
    }
}