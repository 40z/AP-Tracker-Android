package com.movsoft.aptracker.models

/**
 * AppSettings model.
 * Keeps track of the user's tracking settings.
 */
data class AppSettings(var userName: String?, var server: String?, var trackingChannel: String?) {

    /**
     * Valid settings needs to have all properties non-null.
     */
    fun isValid(): Boolean {
        return !userName.isNullOrEmpty() && !server.isNullOrEmpty() && !trackingChannel.isNullOrEmpty()
    }
}
