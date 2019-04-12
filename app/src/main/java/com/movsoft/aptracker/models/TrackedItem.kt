package com.movsoft.aptracker.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.*

class TrackedItem(
    val identifier: String = UUID.randomUUID().toString(),
    var name: String
) {

    override fun equals(other: Any?): Boolean {
        return if (other is TrackedItem) identifier == other.identifier else false
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    //------------------------------------------------------------------------------------------------------------------
    // Custom Deserializer
    //------------------------------------------------------------------------------------------------------------------

    class Deserializer : JsonDeserializer<TrackedItem> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TrackedItem {
            val jObj = json?.asJsonObject ?: return TrackedItem(name = "")
            val identifier = jObj["identifier"]?.asString ?: UUID.randomUUID().toString()
            val name = jObj["name"]?.asString ?: ""
            return TrackedItem(identifier, name)
        }
    }
}