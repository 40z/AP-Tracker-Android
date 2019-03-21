package com.movsoft.aptracker.models

/**
 * Settings model.
 * Keeps track of the user's tracking settings.
 */
data class Settings(var userName: String?, var server: String?, var trackingChannel: String?) {

    /**
     * Valid settings needs to have all properties non-null.
     */
    fun isValid(): Boolean {
        return userName != null && server != null && trackingChannel != null
    }
}
