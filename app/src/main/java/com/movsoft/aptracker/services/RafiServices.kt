package com.movsoft.aptracker.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

interface TrackingServices {
    fun track(item: String, successListener: Response.Listener<String>, errorListener: Response.ErrorListener)
}

class DummyTrackingServices: TrackingServices {
    override fun track(item: String, successListener: Response.Listener<String>, errorListener: Response.ErrorListener) {
        //Do nothing
    }
}

class RafiServices(context: Context): TrackingServices {

    private val queue = Volley.newRequestQueue(context)

    override fun track(item: String, successListener: Response.Listener<String>, errorListener: Response.ErrorListener) {
        val url = "http://68.49.121.20:8090/hubot/aptracker/tracking"
        val jsonBody = JSONObject()
        jsonBody.put("user", "mov1s")
        jsonBody.put("trackeditem", "test")
        jsonBody.put("action", "SINGLE")

        val request = object : StringRequest(Request.Method.POST, url, successListener, errorListener) {
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