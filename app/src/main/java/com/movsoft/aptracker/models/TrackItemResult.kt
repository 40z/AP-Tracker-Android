package com.movsoft.aptracker.models

/**
 * Result model for a TrackingService.trackItem request.
 */
data class TrackItemResult(val status: Status) {

    enum class Status(val value: String) {
        STARTED("started"),
        STOPPED("stopped")
    }
}