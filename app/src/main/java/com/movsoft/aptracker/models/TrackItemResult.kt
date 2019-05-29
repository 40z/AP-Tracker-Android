package com.movsoft.aptracker.models

import com.google.gson.annotations.SerializedName

/**
 * Result model for a TrackingService.trackItem request.
 */
data class TrackItemResult(val status: Status) {

    enum class Status(val value: String) {
        @SerializedName("started")
        STARTED("started"),
        @SerializedName("stopped")
        STOPPED("stopped"),
        @SerializedName("single")
        SINGLE("single")
    }
}