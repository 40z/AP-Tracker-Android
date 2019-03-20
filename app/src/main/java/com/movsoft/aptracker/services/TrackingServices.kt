package com.movsoft.aptracker.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

typealias TrackItemCompletion = (result: Result<Boolean>) -> Unit

/**
 * Services that provide tracking functionality.
 */
interface TrackingServices {
    fun track(item: String, completion: TrackItemCompletion)
}

/**
 * Implementation of TrackingServices that does nothing.
 */
class DummyTrackingServices: TrackingServices {
    override fun track(item: String, completion: TrackItemCompletion) {}
}

/**
 * Implementation of TrackingServices that tracks items with RafiBot.
 */
class RafiTrackingServices(context: Context): TrackingServices {

    private val queue = Volley.newRequestQueue(context)

    override fun track(item: String, completion: TrackItemCompletion) {
        val url = "http://68.49.121.20:8090/hubot/aptracker/tracking"
        val jsonBody = JSONObject()
        jsonBody.put("user", "mov1s")
        jsonBody.put("trackeditem", "test")
        jsonBody.put("action", "SINGLE")

        val request = object : StringRequest(Request.Method.POST, url, Response.Listener<String> {
            completion(Result.success(true))
        }, Response.ErrorListener {
            completion(Result.failure(it))
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray(Charsets.UTF_8)
            }
        }

        queue.add(request)
    }
}