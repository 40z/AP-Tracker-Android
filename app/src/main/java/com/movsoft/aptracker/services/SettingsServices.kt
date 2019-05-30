package com.movsoft.aptracker.services

import android.content.Context
import com.google.gson.GsonBuilder
import com.movsoft.aptracker.models.AppSettings

/**
 * Services that retrieve and persist AppSettings.
 */
interface SettingsServices {
    fun saveSettings(appSettings: AppSettings)
    fun getSettings(): AppSettings
    fun saveLastUsedVersionCode(code: Int)
    fun getLastUsedVersionCode(): Int
}

/**
 * Implementation of SettingsServices that persists AppSettings in SharedPreferences.
 */
class SharedPreferencesSettingsServices(context: Context): SettingsServices {

    private var sharedPrefs = context.getSharedPreferences("settings", 0)

    override fun saveSettings(appSettings: AppSettings) {
        val json = GsonBuilder().create().toJson(appSettings)
        val prefsEditor = sharedPrefs.edit()
        prefsEditor.putString("savedSettings", json)
        prefsEditor.apply()
    }

    override fun getSettings(): AppSettings {
        val json = sharedPrefs.getString("savedSettings", "{ \"trackingChannel\":\"tracking\" }")
        return GsonBuilder().create().fromJson(json, AppSettings::class.java)
    }

    override fun saveLastUsedVersionCode(code: Int) {
        val prefsEditor = sharedPrefs.edit()
        prefsEditor.putInt("lastUsedVersionCode", code)
        prefsEditor.apply()
    }

    override fun getLastUsedVersionCode(): Int {
        return sharedPrefs.getInt("lastUsedVersionCode", 5)
    }
}