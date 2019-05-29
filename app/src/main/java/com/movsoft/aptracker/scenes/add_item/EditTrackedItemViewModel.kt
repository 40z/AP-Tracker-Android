package com.movsoft.aptracker.scenes.add_item

import android.content.Context
import androidx.lifecycle.ViewModel
import com.movsoft.aptracker.R
import com.movsoft.aptracker.models.TrackedItem
import com.movsoft.aptracker.models.TrackedItemSettings
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode.TOGGLE
import com.movsoft.aptracker.services.TrackedItemServices

class EditTrackedItemViewModel(context: Context, private val trackedItemServices: TrackedItemServices): ViewModel(), AddTrackedItemViewModel {

    override val titleText: String = context.getString(R.string.edit_item)
    override val saveText: String = context.getString(R.string.save)

    override var nameText: String? = null
    override var trackingChannelText: String? = null
    override var trackingStyleSelection: TrackingMode = TOGGLE
    lateinit var itemIdentifier: String

    override fun refresh() {
        val item = trackedItemServices.getTrackedItem(itemIdentifier)
        if (item != null) {
            nameText = item.name
            trackingChannelText = item.settings.trackingChannel
            trackingStyleSelection = item.settings.trackingMode
        }
    }

    override fun save() {
        if (nameText.isNullOrEmpty()) { return }
        val channel = if (trackingChannelText.isNullOrEmpty()) null else trackingChannelText!!
        val settings = TrackedItemSettings(channel, trackingStyleSelection)
        val item = TrackedItem(itemIdentifier, nameText!!, settings)
        trackedItemServices.updateTrackedItem(item!!)
    }
}