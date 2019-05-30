package com.movsoft.aptracker.scenes.add_item

import android.content.Context
import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.R
import com.movsoft.aptracker.models.TrackedItem
import com.movsoft.aptracker.models.TrackedItemSettings
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode.TOGGLE
import com.movsoft.aptracker.services.TrackedItemServices

interface AddTrackedItemViewModel {
    val titleText: String
    val saveText: String
    var nameText: String?
    var trackingChannelText: String?
    var trackingStyleSelection: TrackingMode
    fun refresh()
    fun save()
}

class AddTrackedItemViewModelImpl(context: Context, private val trackedItemServices: TrackedItemServices): ViewModel(), AddTrackedItemViewModel {

    override val titleText: String = context.getString(R.string.add_new_item)
    override val saveText: String = context.getString(R.string.add)

    override var nameText: String? = null
    override var trackingChannelText: String? = null
    override var trackingStyleSelection: TrackingMode = TOGGLE

    override fun refresh() {
        //Do nothing
    }

    override fun save() {
        if (nameText.isNullOrEmpty()) { return }
        val channel = if (trackingChannelText.isNullOrEmpty()) null else trackingChannelText!!
        val settings = TrackedItemSettings(channel, trackingStyleSelection)
        val item = TrackedItem(name = nameText!!, settings = settings)
        trackedItemServices.addTrackedItem(item)
    }
}