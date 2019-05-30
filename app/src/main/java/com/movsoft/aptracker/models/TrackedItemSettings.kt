package com.movsoft.aptracker.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

/**
 * Tracked Item Settings Model.
 * Keeps track of an items specific tracking options.
 */
class TrackedItemSettings(var trackingChannel: String? = null, var trackingMode: TrackingMode = TrackingMode.TOGGLE) {

    enum class TrackingMode(val value: String) {
        @SerializedName("TOGGLE")
        TOGGLE("TOGGLE"),
        @SerializedName("REAL SINGLE")
        SINGLE("REAL SINGLE");

        companion object {
            fun fromSerializedName(name: String): TrackingMode? {
                return when (name) {
                    "TOGGLE" -> TOGGLE
                    "REAL SINGLE" -> SINGLE
                    else     -> null
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Custom Deserializer
    //------------------------------------------------------------------------------------------------------------------

    class Deserializer : JsonDeserializer<TrackedItemSettings> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TrackedItemSettings {
            val jObj = json?.asJsonObject ?: return TrackedItemSettings()
            val trackingChannel = jObj["trackingChannel"]?.asString
            val trackingModeRaw = jObj["trackingMode"]?.asString ?: TrackingMode.TOGGLE.value
            val trackingMode = TrackingMode.fromSerializedName(trackingModeRaw) ?: TrackingMode.TOGGLE
            return TrackedItemSettings(trackingChannel, trackingMode)
        }
    }
}