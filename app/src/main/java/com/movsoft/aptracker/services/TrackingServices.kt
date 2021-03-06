package com.movsoft.aptracker.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.movsoft.aptracker.models.TrackItemResult
import com.movsoft.aptracker.models.TrackedItem
import org.json.JSONObject

typealias TrackItemCompletion = (result: Result<TrackItemResult>) -> Unit

/**
 * Services that provide tracking functionality.
 */
interface TrackingServices {
    fun track(item: TrackedItem, completion: TrackItemCompletion)
    fun trackAverage(item: TrackedItem, completion: TrackItemCompletion)
}

/**
 * Implementation of TrackingServices that does nothing.
 */
class DummyTrackingServices: TrackingServices {
    override fun track(item: TrackedItem, completion: TrackItemCompletion) {
        completion(Result.success(TrackItemResult(TrackItemResult.Status.STARTED)))
    }

    override fun trackAverage(item: TrackedItem, completion: TrackItemCompletion) {
        completion(Result.success(TrackItemResult(TrackItemResult.Status.STARTED)))
    }
}

/**
 * Implementation of TrackingServices that tracks items with RafiBot.
 */
class RafiTrackingServices(context: Context, private val settingsServices: SettingsServices): TrackingServices {

    private val queue = Volley.newRequestQueue(context)

    override fun track(item: TrackedItem, completion: TrackItemCompletion) {
        val settings = settingsServices.getSettings()
        val trackingChannel = item.settings.trackingChannel ?: settings.trackingChannel
        val trackingMode = item.settings.trackingMode.value
        val path = "/hubot/aptracker/$trackingChannel"
        val jsonBody = JSONObject()
        jsonBody.put("user", settings.userName)
        jsonBody.put("trackeditem", item.name)
        jsonBody.put("action", trackingMode)
        makeRequest(path, jsonBody, TrackItemResult::class.java, completion)
    }

    override fun trackAverage(item: TrackedItem, completion: TrackItemCompletion) {
        val settings = settingsServices.getSettings()
        val trackingChannel = item.settings.trackingChannel ?: settings.trackingChannel
        val path = "/hubot/aptracker/$trackingChannel"
        val jsonBody = JSONObject()
        jsonBody.put("user", settings.userName)
        jsonBody.put("trackeditem", item.name)
        jsonBody.put("action", "AVERAGE")
        makeRequest(path, jsonBody, TrackItemResult::class.java, completion)
    }

    private fun <T> makeRequest(path: String, postBody: JSONObject, resultClass: Class<T>, completion: (result: Result<T>) -> Unit) {
        val settings = settingsServices.getSettings()

        if (!settings.isValid()) {
            val error = Error("Settings not valid")
            completion(Result.failure(error))
            return
        }

        val url = "${settings.server}${path}"

        val request = object : StringRequest(Request.Method.POST, url, Response.Listener<String> {
            val result = GsonBuilder().create().fromJson(it, resultClass)
            completion(Result.success(result))
        }, Response.ErrorListener {
            completion(Result.failure(it))
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                return postBody.toString().toByteArray(Charsets.UTF_8)
            }
        }

        queue.add(request)
    }
}